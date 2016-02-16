package ufop.smd.gui.activity;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import ufop.smd.R;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import ufop.smd.modelo.Visitapesquisa;
import ufop.smd.persistencia.dao.AbstractDAO;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Activity de visualização de informações de um boletim de pesquisa
 * 
 * @author maycon
 * 
 */
public class VisitaPesquisaVisualizar extends ActivityPaiVisita {

	private EditText etNTub;
	private EditText etNAmo;
	private EditText etDepIns;
	protected Button btNAmo;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);

		setContentView(R.layout.visita_pesquisa);

		inicializaComponentes();
	}

	@Override
	public boolean inicializaComponentes() {

		// Inicializa todos os componentes da activity
		etQua = (EditText) findViewById(R.id.etQua);
		etLog = (EditText) findViewById(R.id.etLog);
		etImo = (EditText) findViewById(R.id.etImo);
		etNTub = (EditText) findViewById(R.id.etNTub);
		etNAmo = (EditText) findViewById(R.id.etNAmo);
		etDepIns = (EditText) findViewById(R.id.etDepIns);
		spTipUn = (Spinner) findViewById(R.id.spTipUn);
		etDepIns = (EditText) findViewById(R.id.etDepIns);
		cbUltVisBol = (CheckBox) findViewById(R.id.cbUltVis);
		cbQC = (CheckBox) findViewById(R.id.cbQC);
		etIdVis = (EditText) findViewById(R.id.etIdVis);
		etIdBol = (EditText) findViewById(R.id.etIdBol);
		etHora = (EditText) findViewById(R.id.etHora);
		btQua = (Button) findViewById(R.id.btQua);
		btLog = (Button) findViewById(R.id.btLog);
		btImo = (Button) findViewById(R.id.btImo);
		btCadAlt = (Button) findViewById(R.id.btCadAlt);
		btLimpar = (Button) findViewById(R.id.btLimpar);
		btNAmo = (Button) findViewById(R.id.btNAmo);

		limparGui();
		addListeners();

		// Captura os elementos do boletim
		getParcel();
		localidade = getLocalidadeBoletim();
		municipio = (Municipio) uDao.getObject(Municipio.class,
				localidade.getIdMunicipio(), null);

		// Preenche o spinner de quadras de acordo com a localidade do boletim
		preencheGui();

		return true;
	}

	@Override
	public boolean addListeners() {
		super.addListeners();

		btNAmo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				carregaNumeroAmostra();
			}
		});

		return true;
	}

	@Override
	public boolean getParcel() {

		Log.d("smd", "Captura parcel em visita de pesquisa.");
		bolTra = null;
		visTra = null;

		Intent it = getIntent();
		if (it != null && it.getExtras() != null) {
			idBoletim = it.getExtras().getInt("idBoletim");
			etIdBol.setText(idBoletim + "");
			bolTra = null;
			bolPes = (Boletimpesquisa) new UtilsDAO(this).getObject(
					Boletimpesquisa.class, idBoletim, null);

			if (it.getExtras().getParcelable("visita") != null) {
				visTra = null;
				visPes = (Visitapesquisa) it.getExtras()
						.getParcelable("visita");
			}
		}

		return true;
	}

	@Override
	public boolean preencheGui() {

		if (visPes != null) {

			// Busca os objetos relacionados a @obj
			UtilsDAO uDao = new UtilsDAO(this);

			// Preenche os Spinners e busca os objetos relacionados a eles
			quadra = (Quadra) uDao.getObject(Quadra.class,
					visPes.getIdQuadra(), null);
			logradouro = (Logradouro) uDao.getObject(Logradouro.class,
					visPes.getIdLogradouro(), null);
			imovel = (Imovel) uDao.getObject(Imovel.class,
					visPes.getIdImovel(), null);

			// Preenche os componentes do layout
			etQua.setText(quadra.getCodigo().toString());
			etLog.setText(logradouro.getNome());
			String strImo = imovel.getNumero() + " - ";
			if (imovel.getComplemento() != null
					&& !imovel.getComplemento().equals("")) {
				strImo += imovel.getComplemento();
			}
			etImo.setText(strImo);
			etNTub.setText(visPes.getNumeroTubitos().toString());
			Componentes.setSelectedItemOnSpinner(spTipUn,
					visPes.getTipoUnidade());
			etDepIns.setText(visPes.getDepositosInspecionados() + "");
			etIdVis.setText(visPes.getIdVisitaPesquisa() + "");
			etIdBol.setText(visPes.getIdBoletimpesquisa() + "");
			etHora.setText(visPes.getHora().toString() + "");
			cbUltVisBol.setChecked(visPes.isUltimaVisitaBoletim());
			cbQC.setChecked(visPes.isQuadraConcluida());

			if (visPes.getNumeroAmostra() != null
					&& (visPes.getNumeroAmostra().toString().length() > 0 && Integer
							.parseInt(visPes.getNumeroAmostra().toString()) != 0)) {
				etNAmo.setText(visPes.getNumeroAmostra().toString());
				etNAmo.setEnabled(true);

			} else {
				etNAmo.setText("");
				etNAmo.setEnabled(false);

			}

			guiAtiva(true);

		} else {

			// Busca o usuário/agente logado na memória
			// Verifica se existem um agente na memória
			if (Login.getAgente() == null) {
				// Sai do sistema
				logoff();
				toast("Não existem nenhum usuário logado.");

				// Caso não exista um agente na memória
			}

			// Preenche demais campos
			etHora.setText(Horas.getHoraAgora().toString());
			etIdBol.setText(idBoletim.toString());

		}

		return true;
	}

	@Override
	public boolean limparGui() {

		Componentes.apagaSpinner(spTipUn);
		Componentes
				.apagaEditText(etDepIns, etQua, etLog, etImo, etNAmo, etNTub);
		Componentes.disabledJComponent(etQua, etLog, etImo, etNAmo);

		municipio = null;
		localidade = null;
		logradouro = null;
		imovel = null;

		guiAtiva(false);

		return true;
	}

	@Override
	public boolean cadastrarAlterar() {

		if (getObjeto() != null) {

			// Captura o objeto na gui
			Visitapesquisa vis = (Visitapesquisa) getObjeto();

			// Verifica se a quadra foi concluída em outra visita
			stopCursor(cursor);
			cursor = dao.consultar("Visitapesquisa", null,
					"idBoletimPesquisa=? AND quadraConcluida=?", new String[] {
							idBoletim.toString(), "1" }, null, null, "hora");
			if (this.verificaQuadraConcluida(cursor, vis.getIdQuadra(),
					this.getIdGui(), "Visitapesquisa")) {
				toast("Esta quadra já foi concluída neste boletim.");
				return false;
			}

			// Verifica se é para cadastrar ou alterar, conferindo se o id está
			// preenchido ou não.
			if (vis.getIdVisitaPesquisa() == null) {

				// Cadastra
				long id = dao.cadastrar("Visitapesquisa",
						vis.getContentValues());
				if (id != 0) {
					
					//Atualiza amostra no bd
					atualizaNumeroAmostraBD(vis);
					
					toast("Visita de pesquisa cadastrado com sucesso.");

					etIdVis.setText(id + "");
					etHora.setText(vis.getHora().toString());
					// finish();

				} else {
					toast("Erro ao cadastrar a visita.");
				}

			} else {
				// Altera
				if (dao.alterar("Visitapesquisa", vis.getContentValues(),
						"idVisitapesquisa=?", new String[] { vis
								.getIdVisitaPesquisa().toString() })) {
					
					//Atualiza amostra no bd
					atualizaNumeroAmostraBD(vis);
					
					toast("Visita alterada com sucesso");
					// finish();

				} else {
					toast("Erro ao alterar a visita.");

				}

			}

			return true;
		}

		toast("Não foi possível cadastrar a visita.");

		return false;
	}

	@Override
	public Object getObjeto() {

		// Verifica se todos os campos foram preenchidos
		if (verificaInformacoesMinimas()) {

			Visitapesquisa vis = new Visitapesquisa();
			vis.setDepositosInspecionados(Integer.parseInt(etDepIns.getText()
					.toString()));
			vis.setIdBoletimpesquisa(Integer.parseInt(etIdBol.getText()
					.toString()));
			vis.setIdImovel(imovel.getIdImovel());
			vis.setIdLogradouro(logradouro.getIdLogradouro());
			vis.setIdQuadra(quadra.getIdQuadra());
			vis.setLarvasAeg(null);
			vis.setLarvasAlb(null);
			vis.setLarvasOut(null);
			vis.setNumeroAmostra(etNAmo.getText().toString().length() > 0 ? Integer
					.parseInt(etNAmo.getText().toString()) : null);
			vis.setNumeroTubitos(etNTub.getText().toString().length() > 0 ? Integer
					.parseInt(etNTub.getText().toString()) : null);
			vis.setTipoUnidade(spTipUn.getSelectedItem().toString());
			vis.setUltimaVisitaBoletim(cbUltVisBol.isChecked());
			vis.setQuadraConcluida(cbQC.isChecked());

			// Define a Hora
			if (etHora.getText().toString().length() == 0) {
				vis.setHora(Horas.getHoraAgora());
			} else {
				vis.setHora(Horas.getTime(etHora.getText().toString()));
			}

			// Verifica a data capturada
			if (vis.getHora() == null) {
				return null;
			}

			// Captura o Id
			if (etIdVis.getText().toString().length() != 0) {
				vis.setIdVisitaPesquisa(Integer.parseInt(etIdVis.getText()
						.toString()));

			}

			return vis;
		}

		return null;
	}

	@Override
	public boolean verificaInformacoesMinimas() {

		if (!super.verificaInformacoesMinimas()) {
			return false;
		}

		if (!verificaPreenchido("Depósitos inspecionados não definido.",
				etDepIns)) {
			return false;
		}

		if (!verificaPreenchido("Nº de Tubitos não definido.", etNTub)) {
			etNAmo.setText("");

			return false;

		} else {
			if (etNAmo.getText().toString().length() < 1
					&& (etNTub.getText().length() > 0 && Integer
							.parseInt(etNTub.getText().toString()) > 0)) {
				carregaNumeroAmostra();

				return true;

			} else if (etNAmo.getText().toString().equals("0")) {
				toast("O número da amostra deve ser maior que zero.");

				return false;
			}
		}

		if (!verificaNumeroAmostra()) {
			toast("Este número de amostra já existe em outra visita.");

			return false;
		}

		return true;
	}

	/**
	 * Atualiza o número da amostra do agente no banco de dados
	 * 
	 * @return
	 */
	public boolean atualizaNumeroAmostraBD(Visitapesquisa visita) {

		// Captura no bd o número da amostra atual do usuário
		Agente agente = Login.getAgente();
		Integer amostraAgente = agente.getNumeroAmostra();
        Integer novaAmostra = null;
        boolean armazenaAmostraAgente = false;
        
        //Verifica se o ano da amostra do agente é o mesmo do boletim
        if (agente.getAnoAmostra().equals(Datas.getYear(bolPes.getData()))) {

            //Então o valor da nova amostra é no mínimo a mesma do agente
            novaAmostra = amostraAgente;
            armazenaAmostraAgente = true; //Verificará se existem visitas do boletim atual com número de amostra maior

            //Caso o ano do boletim seja maior do que da amostra do agente
        } else if (agente.getAnoAmostra() < Datas.getYear(bolPes.getData())) {

            //Armazena no agente o novo ano da amostra
            agente.setAnoAmostra(Datas.getYear(bolPes.getData()));
            armazenaAmostraAgente = true; //Verificará se existem visitas do boletim atual com número de amostra maior

            //Então o valor da nova amostra é zero
            novaAmostra = 0;

            //Caso o ano do boletim seja anterior ao ano da amostra
        } else {
            
            //Então o valor da nova amostra é zero
            novaAmostra = 0;

        }

		// Verifica se o número da amostra da visita é maior do que do usuário
		if (visita.getNumeroAmostra() != null
				&& visita.getNumeroAmostra() > novaAmostra) {
			// Guarda o valor da amostra da visita
			novaAmostra = visita.getNumeroAmostra();
		}

		// Caso a amostra tenha sido alterada, armazena o novo valor no bd e no
		// usuário de login
		if ((!novaAmostra.equals(amostraAgente)) && armazenaAmostraAgente) {
			Login.getAgente().setNumeroAmostra(novaAmostra);
			dao.alterar("Agente", Login.getAgente().getContentValues(),
					"idAgente=?", new String[] { Login.getAgente()
							.getIdAgente().toString() });
		}

		return true;
	}

	/**
	 * Carrega o próximo número de amostra automaticamente para este ano e
	 * agente
	 */
	public void carregaNumeroAmostra() {

		// Verifica se o campo de tubitos está preenchido
		if (etNTub.getText().toString().length() > 0
				&& Integer.parseInt(etNTub.getText().toString()) > 0) {

			// Libera o campo de amostra
			etNAmo.setEnabled(true);

			// Verifica se a visita já está cadastrada e tenta recuperar o
			// número de amostra da mesma
			if (etIdVis.getText().toString().length() > 0) {

				// Carrega no bd a Visita e Verifica se existe algum número de
				// amostra
				stopCursor(cursor);
				cursor = dao.consultar("Visitapesquisa",
						new String[] { "numeroAmostra" }, "idVisitaPesquisa=?",
						new String[] { etIdVis.getText().toString() });
				if (cursor.getString(0) != null
						&& cursor.getString(0).length() > 0) {
					etNAmo.setText(this.getMaiorNumeroAmostra(cursor.getInt(0)));

					return;
				}
			}

			// Caso a visita ainda não esteja cadastrada no bd
			// Carrega no bd o maior número de amostra presente na última visita
			// do agente neste ano
			String ano = new SimpleDateFormat("yyyy").format(bolPes.getData());
			stopCursor(cursor);
			cursor = dao
					.consultar(
							"select v.numeroAmostra from Visitapesquisa v, Boletimpesquisa b where b.idAgente=? AND b.data >='"
									+ ano
									+ "-01-01' AND b.data <='"
									+ ano
									+ "-12-31' order by v.numeroAmostra desc",
							new String[] { Login.getAgente().getIdAgente()
									.toString() });

			Integer amostra = -1;

			// Verifica se foi encontrado alguma visita deste ano, e se há
			// número de amostra no bd
			if (cursor != null && !cursor.isAfterLast()) {
				if (cursor.getString(0) != null) {
					// Captura o maior valor de amostra encontrado no bd para
					// esta data e agente
					amostra = cursor.getInt(0) + 1;
				}
			}

			// Caso não tenha sido encontrado nenhuma visita no bd
			if (amostra.equals(-1)) {
				amostra = 1;
			}

			etNAmo.setText(this.getMaiorNumeroAmostra(Login.getAgente(),
					amostra));

		} else {
			// Libera o campo de amostra
			etNAmo.setEnabled(false);
			etNAmo.setText("");

		}

	}

	/**
	 * Retorna o maior número da amostra, o incremento do encontrado pela Gui ou
	 * o incremento do o número do BD
	 */
	public String getMaiorNumeroAmostra(Agente agente, Integer amostraGui) {

		// Caso o agente tenha sido encontrado, compara e retorna o maior número
		// de amostra
		if (agente != null && agente.getNumeroAmostra() != null) {
			
			//Verifica se o ano do boletim é o mesmo da amostra do agente
			Integer anoBoletim = Datas.getYear(bolPes.getData());
            if (agente.getAnoAmostra().equals(anoBoletim)) {
			
				Integer novaAmostraAgente = agente.getNumeroAmostra() + 1;
				if (novaAmostraAgente > amostraGui) {
					return novaAmostraAgente.toString();
				}
            }
		}

		// Caso não tenha sido encontrado o agente ou o seu número, retorna o
		// número da amostra mesmo
		return amostraGui.toString();

	}

	public String getMaiorNumeroAmostra(Integer amostraGui) {
		return this.getMaiorNumeroAmostra(null, amostraGui);
	}

	/**
	 * Verifica se já existe uma visita deste mesmo agente com um mesmo número
	 * de amostra
	 */
	public boolean verificaNumeroAmostra() {

		stopCursor(cursor);
		if (etIdVis.getText().toString().length() > 0) {
			cursor = dao.consultar("Visitapesquisa",
					new String[] { "idVisitaPesquisa" },
					"idVisitaPesquisa!=? AND numeroAmostra=?", new String[] {
							etIdVis.getText().toString(),
							etNAmo.getText().toString() });

			// Caso tenha encontrado um boletim diferente com o mesmo número de
			// amostra
			if (cursor != null && !cursor.isAfterLast()) {
				// Retorna que o número da amostra é inválido
				return false;
			}

		} else {
			cursor = dao.consultar("Visitapesquisa",
					new String[] { "idVisitaPesquisa" }, "numeroAmostra=?",
					new String[] { etNAmo.getText().toString() });

			// Caso tenha encontrado um outro boletim com o mesmo número de
			// amostra
			if (cursor != null && !cursor.isAfterLast()) {
				// Retorna que o número da amostra é inválido
				return false;
			}
		}

		return true;
	}

}
