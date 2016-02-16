package ufop.smd.controle.sincronizacao;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import ufop.smd.modelo.Quadralogradouro;
import ufop.smd.modelo.Visitapesquisa;
import ufop.smd.modelo.Visitatratamento;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

public class AsyncTaskSincronizacao extends AsyncTaskPai implements
		Serializable {

	private static final long serialVersionUID = 1L;

	private transient Municipio municipio = null;
	private transient EditText text;

	public AsyncTaskSincronizacao(Context context, Activity activity,
			EditText text, Cursor cursor, String tipoSincronizacao) {
		super(context, activity, text, cursor, tipoSincronizacao);
		this.text = text;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		msgProgress = values[0];
		progress.setMessage(msgProgress);

		if (text != null) {
			text.setText(text.getText().toString() + msgProgress + "\n");
		}
		super.onProgressUpdate(values);
	}

	public void setActivity(Activity activity, EditText text) {
		this.activity = activity;
		this.text = text;
		if (getStatus() == Status.RUNNING) {
			progress = new ProgressDialog(activity);
			progress.setMessage(msgProgress);
			progress.show();
			text.setText(msgProgress);
		}
	}

	public boolean sincronizar(String tipoSincronizacao) {

		try {
			// Abre o Progress Dialog
			publishProgress("Procurando Servidor...");

			// Captura no bd a porta, o host e o código do município
			Integer porta = null;
			String ip = null;
			String codMun = "0000000";
			cursor = dao.consultar("configuracao", null, null, null);
			if (cursor != null && !cursor.isAfterLast()) {
				porta = Integer.parseInt(cursor.getString(1));
				ip = cursor.getString(2);
				codMun = cursor.getString(3);

			}
			if (porta == null || ip == null || codMun.equals("0000000")) {
				publishProgress("Não foi encontrado as definições de configurações.");
				return false;
			}

			// Conecta ao servidor
			ConnectionSocket connection = ConnectionSocket.createConnection(ip,
					porta);
			connection.connect();
			DataOutputStream out = connection.getDataOutput();

			// Verifica se a conexão foi realizada
			if (connection != null) {
				publishProgress("Servidor Encontrado!");

				// Verifica o tipo de sincronizacao e executa a mesma
				if (tipoSincronizacao.equals(Constantes.SINC_COMPLETO)) {
					return this.sincronizarTudo(connection, out, codMun);

				} else if (tipoSincronizacao.equals(Constantes.SINC_BOL)) {
					return this.sincronizarBoletins(connection, out, codMun, false);

				}
			}
		} catch (SocketTimeoutException ex) {
			ex.printStackTrace();
			publishProgress("Servidor não encontrado.");

		} catch (ConnectException ex) {
			ex.printStackTrace();
			publishProgress("Servidor Incessível.");

		} catch (IOException ex) {
			ex.printStackTrace();
			publishProgress("Erro ao salvar o croqui.");

		} catch (Exception ex) {
			ex.printStackTrace();
			publishProgress("Erro ao se conectar ao servidor.");

		}

		return false;
	}

	/**
	 * Realiza a sincronização apenas dos boletins com o servidor
	 */
	public boolean sincronizarBoletins(ConnectionSocket connection,
			DataOutputStream out, String codMun, boolean isSincCompleta) throws UnknownHostException,
			IOException {

		// Envia a requisição de sincronização apenas de boletins
		out.writeUTF(Constantes.SINC_BOL);
		publishProgress("Conectado ao Servidor.");

		// Aguarda resposta do servidor se a sincronização será realizada
		msg = connection.getDataInput().readUTF();
		
		//Sincroniza numero e ano da amostra de todos os usuários da apk
		sincronizaAmostraUsuariosAPK(connection.getDataInput(), out);

		// Envia o código do município
		out.writeUTF(codMun);

		// Recebe a resposta do servidor
		msg = connection.getDataInput().readUTF();

		// Verifica se a mensagem recebida foi um OK
		if (msg.equals(Constantes.OK)) {

			publishProgress("Município encontrado");

			// Recebe o município e armazena no bd
			if (!recebeMunicipio(connection.getDataInput())) {
				// Informa ao servidor que a operação foi concluida
				out.writeUTF(Constantes.CANCEL);
				publishProgress("Erro ao armazenar Município.");
				publishProgress("Sincronização Cancelada.");
			}

				// Verifica se a sincronização será realizada
				msg = connection.getDataInput().readUTF();
				if (msg.equals(Constantes.OK)) {
					publishProgress("Sincronizando Boletins...");

					// Busca no bd o município a ser sincronizado
					int qtd = 0;
					// Informa a quantidade de boletins de tratamento a
					// serem sincronizados
					stopCursor(cursor);
					cursor = dao.consultar("Boletimtratamento", null,
							"idMunicipio=?", new String[] { municipio
									.getIdMunicipio().toString() });
					qtd = cursor.getCount();
					out.writeInt(qtd);
					publishProgress("Sincronizando " + qtd
							+ " bol. de tratamento.");

					// Envia cada boletim de tratamento ainda não
					// sincronizado, uma a um, com suas respectivas visitas,
					// deleta após enviados
					while (cursor != null && !cursor.isAfterLast()) {
						Boletimtratamento bol = new Boletimtratamento(cursor);
						// Sincroniza
						if (enviaBoletimTratamento(out,
								connection.getDataInput(), bol)) {
							// Deleta caso a sincronização tenha sido
							// confirmada
							deletaBoletimVisitas(connection.getDataInput(),
									Boletimtratamento.class,
									bol.getIdBoletimTratamento());
						}

						cursor.moveToNext();
					}
					publishProgress("Boletins de tratamento sincronizados.");

					// Informa a quantidade de boletins de pesquisa a serem
					// sincronizados
					stopCursor(cursor);
					cursor = dao.consultar("Boletimpesquisa", null,
							"idMunicipio=?", new String[] { municipio
									.getIdMunicipio().toString() });
					qtd = cursor.getCount();
					out.writeInt(qtd);
					publishProgress("Sincronizando " + qtd
							+ " bol. de pesquisa.");

					// Envia cada boletim de pesquisa ainda nãp
					// sincronizado, uma a um, com suas respectivas visitas,
					// deleta após enviados
					while (cursor != null && !cursor.isAfterLast()) {
						Boletimpesquisa bol = new Boletimpesquisa(cursor);
						// Sincroniza
						if (enviaBoletimPesquisa(out,
								connection.getDataInput(), bol)) {
							// Deleta caso a sincronização tenha sido
							// confirmada
							deletaBoletimVisitas(connection.getDataInput(),
									Boletimpesquisa.class,
									bol.getIdBoletimPesquisa());
						}

						cursor.moveToNext();
					}
					publishProgress("Boletins de pesquisa sincronizados.");
					
				}
				
				// Informa ao servidor que a operação foi concluida
				if(!isSincCompleta){
					out.writeUTF(Constantes.CONCLUIDO);
				}
				publishProgress("Sincronização de Boletins concluída!");
			
		} else if (msg.equals(Constantes.CANCEL)) {

			// Notifica que o município é inexistente
			publishProgress("Código de município inexistente.");

			// Informa ao servidor que a operação foi cancelada
			out.writeUTF(Constantes.CANCEL);
			publishProgress("Sincronização Cancelada.");

		}

		return true;
	}
	
	/**
	 * Sincroniza numero e ano da amostra de todos os usuários da apk
	 * @throws IOException 
	 */
	public boolean sincronizaAmostraUsuariosAPK(DataInputStream in, DataOutputStream out) throws IOException{
		
		//Consulta no bd os agentes da apk
		stopCursor(cursor);
		cursor = dao.consultar("Agente", null, null, null);
		
		publishProgress("Sincronizando amostras.");
		
		//sincroniza a amostra de cada agente com o servidor
		while (cursor != null && !cursor.isAfterLast()) {

			//envia um ok para sincronizar este usuario
			out.writeUTF(Constantes.OK);
			
			//Instancia o usuário
			Agente age = new Agente(cursor);
			
			//envia o id do usuário
			out.writeInt(age.getIdAgente());
			
			//envia o número da amostra do usuário
			out.writeInt(age.getNumeroAmostra());
			
			//envia o ano da amostra do usuário
			out.writeInt(age.getAnoAmostra());
			
			//recebe o novo número de amostra
			Integer servAmostra = in.readInt();
			
			//recebe o novo ano de amostra
			Integer servAno = in.readInt();
			
			//Caso a amostra ou o ano tenham sido alterados
			if(!age.getAnoAmostra().equals(servAno) || !age.getNumeroAmostra().equals(servAmostra)){
				//Altera no bd o agente com a nova amostra e ano
				age.setNumeroAmostra(servAmostra);
				age.setAnoAmostra(servAno);
				dao.alterar("Agente", age.getContentValues(), "idAgente=?", new String[]{age.getIdAgente().toString()});
			}
			
			cursor.moveToNext();
		}
		
		//Envia um cancel para informar que a operação foi encerrada
		out.writeUTF(Constantes.CANCEL);
		
		publishProgress("Amostra sincronizadas.");
		
		
		return true;
	}

	/**
	 * Realiza a sincronização completa com o servidor
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public boolean sincronizarTudo(ConnectionSocket connection,
			DataOutputStream out, String codMun) throws UnknownHostException,
			IOException {

		// Envia a requisição de sincronização completa
		out.writeUTF(Constantes.SINC_COMPLETO);
		publishProgress("Conectado ao Servidor.");

		// Aguarda resposta do servidor se a sincronização será
		// realizada
		msg = connection.getDataInput().readUTF();

		// Verifica se a sincronização será realizada
		if (msg.equals(Constantes.OK)) {
			publishProgress("Sincronizando...");

			// Envia o código do município
			out.writeUTF(codMun);

			// Recebe a resposta do servidor
			msg = connection.getDataInput().readUTF();

			// Verifica se a mensagem recebida foi um OK
			if (msg.equals(Constantes.OK)) {

				publishProgress("Município encontrado");

				// Recebe o município e armazena no bd
				if (!recebeMunicipio(connection.getDataInput())) {
					// Informa ao servidor que a operação foi concluida
					out.writeUTF(Constantes.CANCEL);
					publishProgress("Erro ao armazenar Município.");
					publishProgress("sincronização Cancelada.");

				}

				// Recebe a quantidade de localidades
				Integer qtd = connection.getDataInput().readInt();
				publishProgress("Sincronizando " + qtd + " localidades.");

				// Pega no bd todas as localidades que tem croquis em
				// anexo
				List<Localidade> listLoc = new ArrayList<Localidade>();
				stopCursor(cursor);
				cursor = dao.consultar("Localidade", null,
						"idMunicipio=? AND croqui=1", new String[] { municipio
								.getIdMunicipio().toString() });
				while (cursor != null && !cursor.isAfterLast()) {
					listLoc.add(new Localidade(cursor));
					cursor.moveToNext();
				}

				// Recebe cada localidade, uma a uma e armazena ou
				// altera no bd
				for (int index = 0; index < qtd; index++) {
					recebeLocalidade(connection.getDataInput());

				}
				publishProgress("Localidades sincronizadas.");

				// Recebe a qtd de lograouros
				qtd = connection.getDataInput().readInt();
				publishProgress("Sincronizando " + qtd + " logradouros.");

				// Recebe cada logradouro, um a um e armazena no bd
				for (int index = 0; index < qtd; index++) {
					recebeLogradouro(connection.getDataInput());
				}
				publishProgress("Logradouros sincronizados.");

				// Recebe a qtd de quadras
				qtd = connection.getDataInput().readInt();
				publishProgress("Sincronizando " + qtd + " quadras.");

				// Recebe cada quadra, uma a uma e armazena no bd
				for (int index = 0; index < qtd; index++) {
					recebeQuadra(connection.getDataInput());
				}
				publishProgress("Quadras sincronizadas.");

				// Recebe a qtd de imóveis
				qtd = connection.getDataInput().readInt();
				publishProgress("Sincronizando " + qtd + " imóveis.");

				// Recebe cada imóvel, um a um e armazena no bd
				for (int index = 0; index < qtd; index++) {
					recebeImovel(connection.getDataInput());
				}
				publishProgress("Imóveis sincronizados.");

				// Sincroniza os Boletins
				sincronizarBoletins(connection, out, codMun, true);

				// Sincronizar Croquis
				publishProgress("Sincronizando Croquis (Esta etapa pode demorar)!");
				sincronizaCroquis(municipio, listLoc, connection);

				// Informa ao servidor que a operação foi concluida
				out.writeUTF(Constantes.CONCLUIDO);
				publishProgress("sincronização Concluída!");

				// Caso a mensagem seja um CANCEL
			} else if (msg.equals(Constantes.CANCEL)) {

				// Notifica que o município é inexistente
				publishProgress("Código de município inexistente.");

				// Informa ao servidor que a operação foi cancelada
				out.writeUTF(Constantes.CANCEL);
				publishProgress("sincronização Cancelada.");

			}

			// Informa que a sincronização não será realizada
		} else {

			// Informa ao servidor que a operação foi concluida
			out.writeUTF(Constantes.CANCEL);
			publishProgress("sincronização Cancelada.");

		}

		return true;

	}

	/**
	 * Sincroniza todos os croquis da cidade
	 * 
	 * @throws IOException
	 */
	public boolean sincronizaCroquis(Municipio mun, List<Localidade> listLoc,
			ConnectionSocket connection) throws IOException {

		DataInputStream in = connection.getDataInput();
		DataOutputStream out = connection.getDataOutput();

		// Envia um OK - Para o servidor ficar atento a sincronização de croquis
		out.writeUTF(Constantes.OK);
		
		// Captura todas as localidades do município no bd (já sincronizadas)
		stopCursor(cursor);
		cursor = dao.consultar("Localidade", null, "idMunicipio=?",
				new String[] { mun.getIdMunicipio().toString() });

		// Percorre cada localidade do bd sincronizado
		while (cursor != null && !cursor.isAfterLast()) {
			Localidade sincLoc = new Localidade(cursor);
			Localidade locAntes = getLocalidadePorId(listLoc,
					sincLoc.getIdLocalidade());

			// Caso a localidade tenha croqui
			if (sincLoc.isCroqui()) {

				// Caso a localidade já existia no bd
				if (locAntes != null) {
					// Verifica se o croqui é diferente de antes
					if (!sincLoc.getDataCroqui().equals(
							locAntes.getDataCroqui())
							|| !sincLoc.getHoraCroqui().equals(
									locAntes.getHoraCroqui())) {

						// Salva o novo croqui
						recebeCroqui(sincLoc.getIdLocalidade(), connection);
					}

					// Caso a localidade seja nova no bd
				} else {

					// Salva o croqui
					recebeCroqui(sincLoc.getIdLocalidade(), connection);
				}

				// Caso a localidade não tenha croqui
			} else {

				// Verifica se a localidade existia antes
				if (locAntes != null) {
					// Verifica se a localidade tinha croqui
					if (locAntes.isCroqui()) {
						// Deleta o croqui
						deletaCroqui(locAntes.getIdLocalidade(), in, out);
					}
				}

			}

			cursor.moveToNext();
		}

		// Envia um OK - Para o servidor encerrar a sincronização de croquis
		out.writeUTF(Constantes.CONCLUIDO);

		return true;
	}

	/**
	 * Recebe um croqui via Socket e anexa a sua localidade
	 * 
	 * @throws IOException
	 */
	public boolean recebeCroqui(Integer idLocalidade,
			ConnectionSocket connection) throws IOException {

		// Abre os devidos canais de comunicação
		DataOutputStream out = connection.getDataOutput();
		DataInputStream in = connection.getDataInput();
		InputStream ins = connection.getIns();

		// Envia um SINC_CROQUI - avisando ao servidor para sincronizar um
		// croqui
		out.writeUTF(Constantes.SINC_CROQUI);

		// Envia idLocalidade
		out.writeInt(idLocalidade);

		// Recebe o tamanho do arquivo
		long tamanho = in.readLong();
		Log.d("ufop.smd", "tamanho=" + tamanho + " idLocalidade=" + idLocalidade);

		// FileOutputStream para salvar o arquivo
		File path = new File(Environment.getExternalStorageDirectory()
				+ Constantes.DIRETORIO_CROQUIS);
		path.mkdirs();
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(new File(path, idLocalidade + ".png")));

		// Recebe os bytes do croqui
		byte[] buffer = new byte[Constantes.BUFFER_SIZE];

		// Envia os bytes
		Integer leitura = null;
		Integer countAnterior = 0;
		Integer count = 0;
		do {
			
			leitura = ins.read(buffer, 0, buffer.length);
			countAnterior = count;
			count += leitura;
			Log.d("ufop.smd", "count=" + count + " countAnterior=" + countAnterior +  " tamanho=" + tamanho + " leitura=" + leitura.toString());
			bos.write(buffer, 0, leitura);
			bos.flush();
			

		} while (leitura != -1 && count < tamanho && count != countAnterior);

		// Fecha o arquivo
		bos.close();

		return true;
	}

	/**
	 * Deleta um croqui
	 */
	public boolean deletaCroqui(Integer idLocalidade, DataInputStream in,
			DataOutputStream out) {

		String path = Environment.getExternalStorageDirectory()
				+ Constantes.DIRETORIO_CROQUIS + idLocalidade + ".png";
		File f1 = new File(path);
		boolean success = false;

		try {
			f1.delete();
		} catch (Exception er) {
			er.printStackTrace();
		}

		return true;
	}

	/**
	 * Envia via Socket um boletim de tratamento com suas visitas, após isto
	 * deleta esses objetos
	 * 
	 * @throws IOException
	 */
	public boolean enviaBoletimTratamento(DataOutputStream out,
			DataInputStream in, Boletimtratamento bol) throws IOException {

		// Envia o boletim
		// idBoletimTratamento - não ENVIA
		// idAgente
		out.writeInt(bol.getIdAgente());
		// idLocalidade
		out.writeInt(bol.getIdLocalidade());
		// idMunicipio
		out.writeInt(bol.getIdMunicipio());
		// coordenadorRegional
		out.writeUTF(bol.getCoordenadorRegional().toString());
		// grs
		out.writeUTF(bol.getGrs());
		// categoria
		out.writeUTF(bol.getCategoria());
		// turma
		out.writeUTF(bol.getTurma());
		// data
		String data = bol.getData() == null ? "" : Datas.getText(bol.getData(),
				"yyyy-MM-dd");
		out.writeUTF(data);
		// semanaEpid
		out.writeUTF(bol.getSemanaEpid());
		// numeroAtividade
		out.writeUTF(bol.getNumeroAtividade());
		// tipoAtividade
		out.writeUTF(bol.getTipoAtividade());
		// sincronizadoEm - não ENVIA
		// ativo - não ENVIA - SEMPRE é SINCRONIZADO COMO TRUE

		// Recebe uma confirmação do servidor
		msg = in.readUTF();
		if (!msg.equals(Constantes.OK)) {
			// Interrompe a sincronização
			publishProgress("Mensagem do servidor não recebida.");
			publishProgress("sincronização cancelada!");

			return false;
		}

		// Envia a qtd de visitas deste boletim
		Cursor cursor = dao.consultar("Visitatratamento", null,
				"idBoletimTratamento=?", new String[] { bol
						.getIdBoletimTratamento().toString() });
		out.writeInt(cursor.getCount());

		// Envia todas as visitas do boletim (já com o novo id)
		while (cursor != null && !cursor.isAfterLast()) {
			Visitatratamento vis = new Visitatratamento(cursor);
			// idVisitatratamento (não ENVIA)
			// idBoletimtratamento (não ENVIA - NOVO ID DO BOLETIM - ID DO
			// SERVIDOR)
			// idQuadra
			out.writeInt(vis.getIdQuadra());
			// idLogradouro
			out.writeInt(vis.getIdLogradouro());
			// idImovel
			out.writeInt(vis.getIdImovel());
			// tipoUnidade
			out.writeUTF(vis.getTipoUnidade());
			// inseticidaLarvicida
			out.writeInt(vis.getInseticidaLarvicida());
			// inseticidaAdulticida
			out.writeInt(vis.getInseticidaAdulticida());
			// pendenciaRec
			out.writeBoolean(vis.getPendenciaRec());
			// pendenciaFech
			out.writeBoolean(vis.getPendenciaFech());
			// pendenciaResg
			out.writeBoolean(vis.getPendenciaResg());
			// despositosTratados
			out.writeInt(vis.getDepositosTratados());
			// ultimaVisitaBoletim
			out.writeBoolean(vis.isUltimaVisitaBoletim());
			// quadraConcluida
			out.writeBoolean(vis.isQuadraConcluida());
			// hora
			out.writeUTF(vis.getHora().toString());

			// Recebe a confirmação para continuar
			msg = in.readUTF();
			if (!msg.equals(Constantes.OK)) {
				// Interrompe a sincronização
				publishProgress("Mensagem do servidor não recebida.");
				publishProgress("sincronização cancelada!");

				return false;
			}

			cursor.moveToNext();

		}

		return true;
	}

	/**
	 * Envia via Socket um boletim de tratamento com suas visitas, após isto
	 * deleta esses objetos
	 * 
	 * @throws IOException
	 */
	public boolean enviaBoletimPesquisa(DataOutputStream out,
			DataInputStream in, Boletimpesquisa bol) throws IOException {

		// Envia o boletim
		// idBoletimPesquisa - não ENVIA
		// idAgente
		out.writeInt(bol.getIdAgente());
		// idLocalidade
		out.writeInt(bol.getIdLocalidade());
		// idMunicipio
		out.writeInt(bol.getIdMunicipio());
		// coordenadorRegional
		out.writeUTF(bol.getCoordenadorRegional().toString());
		// grs
		out.writeUTF(bol.getGrs());
		// categoria
		out.writeUTF(bol.getCategoria());
		// turma
		out.writeUTF(bol.getTurma());
		// data
		String data = bol.getData() == null ? "" : Datas.getText(bol.getData(),
				"yyyy-MM-dd");
		out.writeUTF(data);
		// semanaEpid
		out.writeUTF(bol.getSemanaEpid());
		// numeroAtividade
		out.writeUTF(bol.getNumeroAtividade());
		// liAmostra
		out.writeInt(bol.getLiAmostra());
		// tipoAtividade
		out.writeUTF(bol.getTipoAtividade());
		// sincronizadoEm - não ENVIA
		// ativo - não ENVIA - SEMPRE é SINCRONIZADO COMO TRUE

		// Recebe uma confirmação do servidor
		msg = in.readUTF();
		if (!msg.equals(Constantes.OK)) {
			// Interrompe a sincronização
			publishProgress("Mensagem do servidor não recebida.");
			publishProgress("sincronização cancelada!");

			return false;
		}

		// Envia a qtd de visitas deste boletim
		Cursor cursor = dao.consultar("Visitapesquisa", null,
				"idBoletimPesquisa=?", new String[] { bol
						.getIdBoletimPesquisa().toString() });
		out.writeInt(cursor.getCount());

		// Envia todas as visitas do boletim (já com o novo id)
		while (cursor != null && !cursor.isAfterLast()) {
			Visitapesquisa vis = new Visitapesquisa(cursor);

			// idVisitapesquisa (não ENVIA)
			// idBoletimpesquisa (não ENVIA - NOVO ID DO BOLETIM - ID DO
			// SERVIDOR)
			// idQuadra
			out.writeInt(vis.getIdQuadra());
			// idLogradouro
			out.writeInt(vis.getIdLogradouro());
			// idImovel
			out.writeInt(vis.getIdImovel());
			// tipoUnidade
			out.writeUTF(vis.getTipoUnidade());
			// numeroTubitos
			out.writeInt(vis.getNumeroTubitos());
			// numeroAmostra
			out.writeInt(vis.getNumeroAmostra());
			// larvasAeg - não ENVIA -  SEMPRE NULL NA APK
			// larvasAlb - não ENVIA -  SEMPRE NULL NA APK
			// larvasOut - não ENVIA -  SEMPRE NULL NA APK
			// examinadoLaboratorio
			out.writeBoolean(vis.isExaminadoLaboratorio());
			// depositosInspecionados
			out.writeInt(vis.getDepositosInspecionados());
			// ultimaVisitaBoletim
			out.writeBoolean(vis.isUltimaVisitaBoletim());
			// quadraConcluida
			out.writeBoolean(vis.isQuadraConcluida());
			// hora
			out.writeUTF(vis.getHora().toString());

			// Recebe a confirmação para continuar
			msg = in.readUTF();
			if (!msg.equals(Constantes.OK)) {
				// Interrompe a sincronização
				publishProgress("Mensagem do servidor não recebida.");
				publishProgress("sincronização cancelada!");

				return false;
			}

			cursor.moveToNext();

		}

		return true;
	}

	/**
	 * Deleta um boletim de tratamento ou boletim de pesquisa e suas visitas
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean deletaBoletimVisitas(DataInputStream in, Class classe,
			Integer idBoletim) throws IOException {

		// Recebe uma confirmação do servidor
		msg = in.readUTF();
		if (!msg.equals(Constantes.OK)) {
			// Interrompe a sincronização
			publishProgress("Mensagem do servidor não recebida.");
			publishProgress("sincronização cancelada!");

			return false;
		}

		if (classe.equals(Boletimtratamento.class)) {

			// Deleta todas as visitas do boletim
			dao.excluir("Visitatratamento", "idBoletimtratamento=?",
					new String[] { idBoletim.toString() });
			// Deleta o boletim
			dao.excluir("Boletimtratamento", "idBoletimtratamento=?",
					new String[] { idBoletim.toString() });

		} else if (classe.equals(Boletimpesquisa.class)) {

			// Deleta todas as visitas do boletim
			dao.excluir("Visitapesquisa", "idBoletimpesquisa=?",
					new String[] { idBoletim.toString() });
			// Deleta o boletim
			dao.excluir("Boletimpesquisa", "idBoletimpesquisa=?",
					new String[] { idBoletim.toString() });

		}

		return true;
	}

	/**
	 * Recebe município via Socket e salva no bd
	 * 
	 * @throws IOException
	 */
	public boolean recebeMunicipio(DataInputStream in) throws IOException {

		Municipio mun = new Municipio();
		// id
		mun.setIdMunicipio(in.readInt());
		// codigo
		mun.setCodigo(in.readUTF());
		// nome
		mun.setNome(in.readUTF());
		// estado
		mun.setEstado(in.readUTF());

		// Armazena no bd
		salvaOuAltera("Municipio", "idMunicipio", mun.getIdMunicipio()
				.toString(), mun.getContentValues());
		municipio = mun;

		publishProgress("Município '" + mun.getNome() + "'.");

		return true;
	}

	/**
	 * Recebe uma localidade via Socet e salva no bd
	 */
	public boolean recebeLocalidade(DataInputStream in) throws IOException {

		Localidade loc = new Localidade();
		// idLocalidade
		loc.setIdLocalidade(in.readInt());
		// idMunicipio
		loc.setIdMunicipio(in.readInt());
		// codigo
		loc.setCodigo(in.readInt());
		// nome
		loc.setNome(in.readUTF());
		// isCroqui
		loc.setCroqui(in.readBoolean());
		// dataCroqui
		String str = in.readUTF();
		Date data = str.equals("") ? null : Datas.getDate(str);
		loc.setDataCroqui(data);
		// horaCroqui
		str = in.readUTF();
		Time hora = str.equals("") ? null : Horas.getTime(str);
		loc.setHoraCroqui(hora);

		// Armazena no bd
		salvaOuAltera("Localidade", "idLocalidade", loc.getIdLocalidade()
				.toString(), loc.getContentValues());

		return true;
	}

	public boolean recebeLogradouro(DataInputStream in) throws IOException {

		Logradouro log = new Logradouro();
		// idLogradouro
		log.setIdLogradouro(in.readInt());
		// idMunicipio
		log.setIdMunicipio(in.readInt());
		// Nome
		log.setNome(in.readUTF());

		// Armazena no bd
		salvaOuAltera("Logradouro", "idLogradouro", log.getIdLogradouro()
				.toString(), log.getContentValues());

		return true;
	}

	public boolean recebeQuadra(DataInputStream in) throws IOException {

		Quadra qua = new Quadra();
		// idQuadra
		qua.setIdQuadra(in.readInt());
		// idLocalidade
		qua.setIdLocalidade(in.readInt());
		// codigo
		qua.setCodigo(in.readInt());

		// Armazena no bd
		salvaOuAltera("Quadra", "idQuadra", qua.getIdQuadra().toString(),
				qua.getContentValues());

		// Recebe a quantidade de quadralogradouro desta quadra
		int qtd = in.readInt();

		// Recebe cada quadralogradouro desta quadra
		for (int index = 0; index < qtd; index++) {
			Quadralogradouro ql = new Quadralogradouro();
			// idQuadraLogradouro
			ql.setIdQuadraLogradouro(in.readInt());
			// idQuadra
			ql.setIdQuadra(in.readInt());
			// idLogradouro
			ql.setIdLogradouro(in.readInt());

			// Salva no bd
			salvaOuAltera("Quadralogradouro", "idQuadraLogradouro", ql
					.getIdQuadraLogradouro().toString(), ql.getContentValues());
		}

		return true;
	}

	public boolean recebeImovel(DataInputStream in) throws IOException {

		Imovel imo = new Imovel();
		// idImovel
		imo.setIdImovel(in.readInt());
		// idQuadra
		imo.setIdQuadra(in.readInt());
		// idLogradouro
		imo.setIdLogradouro(in.readInt());
		// numero
		imo.setNumero(in.readUTF());
		// complemento
		imo.setComplemento(in.readUTF());

		// Salva no bd
		salvaOuAltera("Imovel", "idImovel", imo.getIdImovel().toString(),
				imo.getContentValues());

		return true;
	}

	/**
	 * Verifica se o objeto já existe no bd, salvando-o ou alterando
	 */
	public void salvaOuAltera(String nomeObjeto, String nomeId,
			String idOuCodigo, ContentValues values) {

		// Verifica se a localidade já existe no bd
		stopCursor(cursor);
		cursor = dao.consultar(nomeObjeto, null, nomeId + "=?",
				new String[] { idOuCodigo });
		// Caso exista altera no bd
		if (cursor != null && !cursor.isAfterLast()) {
			dao.alterar(nomeObjeto, values, nomeId + "=?",
					new String[] { idOuCodigo });

			// Caso não exista salva no bd
		} else {
			dao.cadastrar(nomeObjeto, values);

		}

	}

	/**
	 * Percorre uma lista de Localidades e retorna a localidade com o id
	 * procurado
	 */
	public Localidade getLocalidadePorId(List<Localidade> listLoc, Integer id) {

		for (int index = 0; index < listLoc.size(); index++) {
			if (listLoc.get(index).getIdLocalidade().equals(id)) {
				return listLoc.get(index);
			}
		}

		return null;

	}

	/**
	 * Fecha o cursor da classe e o banco de dados
	 * 
	 * @param cursor
	 */
	protected void stopCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

}
