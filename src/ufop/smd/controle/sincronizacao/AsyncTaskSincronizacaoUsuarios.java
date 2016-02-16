package ufop.smd.controle.sincronizacao;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Agente;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class AsyncTaskSincronizacaoUsuarios extends AsyncTaskPai implements Serializable {

	private static final long serialVersionUID = 1L;

	public AsyncTaskSincronizacaoUsuarios(Context context, Activity activity, Cursor cursor) {
		super(context, activity, cursor);
	}
	
	/**
	 * Sincroniza apenas os usuários com o servidor
	 */
	public boolean sincronizar(String tipoSincronizacao){

		int qtd = 0;
		
		try{
			//Abre o Progress Dialog
			publishProgress("Procurando Servidor...");

			//Captura no bd a porta e o host
			Integer porta = null;
			String ip = null;
		    stopCursor(cursor);
		    cursor = dao.consultar("configuracao", null, null, null);
		    if(cursor != null && !cursor.isAfterLast()){
		    	porta = Integer.parseInt(cursor.getString(1));
		    	ip = cursor.getString(2);
		    	
		    }
		    
		    if(porta==null || ip==null){
		    	publishProgress("Não foi encontrado o Ip e a Porta da conexão.");
		    	return false;
		    }
			
			//Conecta ao servidor
			ConnectionSocket connection = ConnectionSocket.createConnection(ip, porta);
			connection.connect();


			//Verifica se a conexão foi realizada
			if(connection != null){
				publishProgress("Servidor Encontrado!");

				//Envia a requisição de sincronização apenas de usuários
				DataOutputStream out = connection.getDataOutput();
				out.writeUTF(tipoSincronizacao);

				//Aguarda resposta do servidor
				publishProgress("Aguardando resposta.");
				msg = connection.getDataInput().readUTF();

				//Verifica se a mensagem recebida foi um OK
				if(msg.equals(Constantes.OK)){

					//Exclui o bd de usuarios do Android
					publishProgress("Removendo usuários.");
					if(dao.excluir("Agente", "idAgente > ?", new String[]{"0"})){
						publishProgress("Usuários removidos.");

						//Informa ao servidor para enviar a quantidade de usuários
						out.writeUTF(Constantes.OK);

						//Recebe a quantidade de usuários a serem sincronizados
						qtd = connection.getDataInput().readInt();
						
						if(qtd > 0){
							publishProgress("Cadastrando usuários.");

						} else {
							publishProgress("Não existem usuários a serem sincronizados.");
						}

						//Recebe cada objeto enviado pelo servidor, um por um, e salva no bd
						for(int index = 0; index < qtd; index++){

							//Recebe a confirmação do servidor para receber um novo objeto
							msg = connection.getDataInput().readUTF();
							
							//Recebe cada atributo do objeto
							Agente age = new Agente();
							//id
							age.setIdAgente(connection.getDataInput().readInt());
							//codigo
							age.setCodigo(connection.getDataInput().readInt());
							//nome
							age.setNome(connection.getDataInput().readUTF());
							//ativo
							age.setAtivo(connection.getDataInput().readBoolean());
							//login
							age.setLogin(connection.getDataInput().readUTF());
							//senha
							age.setSenha(connection.getDataInput().readUTF());
							//dataCadastro
							age.setDataCadastro(Datas.getDate(connection.getDataInput().readUTF()));
							//horaCadastro
							age.setHoraCadastro(Horas.getTime(connection.getDataInput().readUTF()));
							//numeroAmostra
							age.setNumeroAmostra(connection.getDataInput().readInt());
							//anoAmostra
							age.setAnoAmostra(connection.getDataInput().readInt());

							//Cadastra no bd
							dao.cadastrar("Agente", age.getContentValues());

						}
						
					} else {
						publishProgress("Erro ao remover usuários.");

						//Envia mensagem de erro para o servidor
						out.writeUTF(Constantes.CANCEL);
					}

					//Caso a mensagem seja diferente
				} else {
					//Informa o usuário que a operação não poderá ser realizada
					publishProgress("A sincronização não poderá ser realizada!");
					
					//Envia mensagem de erro para o servidor
					out.writeUTF(Constantes.CANCEL);

				}

				//Informa ao servidor que a operação foi concluida
				out.writeUTF(Constantes.CONCLUIDO);
				
				if(qtd > 0){
					publishProgress("Usuários sincronizados com sucesso.");
					
				} else {
					publishProgress("Não existem usuários para serem sincronizados.");
				}
				
				Thread.sleep(3000);

				return true;

			} 
		} catch(SocketTimeoutException ex){
			ex.printStackTrace();
			publishProgress("Servidor não encontrado!");
			

		} catch(ConnectException ex){
			ex.printStackTrace();
			publishProgress("Servidor Inacessível!");
			
		} catch(Exception ex){
			ex.printStackTrace();
			publishProgress("Erro ao se conectar ao servidor!");

		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}

		return false;
	}
	


}
