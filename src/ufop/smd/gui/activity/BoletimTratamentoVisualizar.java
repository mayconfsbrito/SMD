package ufop.smd.gui.activity;

import java.util.StringTokenizer;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Activity de visualização de um boletim de tratamento
 * @author maycon
 *
 */
public class BoletimTratamentoVisualizar extends ActivityPaiBoletim {

	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);

		setContentView(R.layout.boletim_tratamento_visualizacao);

		inicializaComponentes();
	}

	@Override
	public boolean getParcel(){

		Boletimtratamento bol = null;

		Intent it = getIntent();
		if(it != null && it.getExtras() != null){
			Log.d("smd", "Buscando parcel de Boletim de tratamento.");
			bol = (Boletimtratamento) it.getExtras().getParcelable("boletim");

		}

		preencheGui(bol);

		return true;
	}

	@Override
	public boolean preencheGui(Object obj){

		Boletimtratamento bol = (Boletimtratamento) obj;
		
		if(bol != null){
			//Busca os municipios, localidades e agentes relacionados a este boletim
			UtilsDAO uDao = new UtilsDAO(this);
			municipio = (Municipio) uDao.getObject(Municipio.class, bol.getIdMunicipio(), null);
			localidade = (Localidade) uDao.getObject(Localidade.class, bol.getIdLocalidade(), null);
			Log.d("ufop.smd", "idAgente Bol=" + bol.getIdAgente() + " Static=" + Login.getAgente().getIdAgente());
			Agente age = (Agente) uDao.getObject(Agente.class, bol.getIdAgente(), null);

			etCoReg.setText(bol.getCoordenadorRegional());
			etGrsNome.setText(bol.getGrs());
			etMunCod.setText(municipio.getCodigo());
			etMunNome.setText(municipio.getNome());
			etLocCod.setText(localidade.getCodigo().toString());
			etLocNome.setText(localidade.getNome());
			etAgeId.setText(age.getIdAgente().toString());
			etAgeNome.setText(age.getNome());
			
			StringTokenizer tok1 = new StringTokenizer(bol.getNumeroAtividade(), "/");
	        etNAtivNum.setText(tok1.nextToken());
			etNAtivAno.setText(tok1.nextToken("").replaceAll("/", ""));
	        
			StringTokenizer tok2 = new StringTokenizer(bol.getSemanaEpid(), "/");
			etSemEpidNum.setText(tok2.nextToken());
			etSemEpidAno.setText(tok2.nextToken("").replaceAll("/", ""));
			
			etId.setText(bol.getIdBoletimTratamento().toString());
			etData.setText(Datas.getText(bol.getData(), "dd/MM/yyyy"));
			etTurma.setText(bol.getTurma());
			Componentes.setSelectedItemOnSpinner(spCat, bol.getCategoria().toString());
			Componentes.setSelectedItemOnSpinner(spTipAtv, bol.getTipoAtividade());
			
			guiAtiva(true);
			
		} else {

			//Busca o usuário/agente logado na memória
			//Verifica se existem um agente na memória
			if(Login.getAgente() != null){
				etAgeId.setText(Login.getAgente().getIdAgente().toString());
				etAgeNome.setText(Login.getAgente().getNome());

				//Caso não exista um agente na memória
			} else {
				//Sai do sistema
				logoff();
				toast("Não existem nenhum usuário logado.");
			}
			
			//Preenche a data
			etData.setText(Datas.getText(Datas.getDataHoje(), "dd/MM/yyyy"));
		}

		return true;
	}
	
	@Override
	public boolean visitas(){

		//Verifica se o boletim já foi cadastrado
		if(etId.getText().toString().length() > 0){

			Log.d("smd", "Parcelando em Boletim.");
			//Caso o boletim esteja cadastrado, inicia a Activity de Visitas
			Intent it = new Intent(this, VisitaTratamentoListagem.class);
			it.putExtra("idBoletim", Integer.parseInt(etId.getText().toString()));
			startActivityForResult(it, Constantes.TELA_VISITA_TRATAMENTO_LISTAGEM);

			return true;

		} else {
			//Caso o boletim não tenha sido cadastrado
			toast("O boletim deve ser cadastrado antes de visualizar visitas.");
			return false;
		}
	}
	
	@Override
	public boolean cadastrarAlterar(){

		//Captura o objeto na gui
		Boletimtratamento bol = (Boletimtratamento) getObjeto();

		//Verifica se a interface está completa
		if(bol != null){

			//Verifica se é para cadastrar ou alterar, conferindo se o id está preenchido ou não.
			if(bol.getIdBoletimTratamento() == null){

				//Cadastra
				long id = dao.cadastrar("Boletimtratamento", bol.getContentValues());
				if(id != 0){
					toast("Boletim de tratamento cadastrado com sucesso.");

					etId.setText(id + "");
					//finish();

				} else {
					toast("Erro ao cadastrar o boletim.");
				}

			} else {
				//Altera
				if(dao.alterar("Boletimtratamento", bol.getContentValues(), "idBoletimtratamento=?", new String[]{bol.getIdBoletimTratamento().toString()})){
					toast("Boletim alterado com sucesso");
					//finish();

				} else {
					toast("Erro ao alterar o boletim.");

				}

			}

			return true;

		}

		toast("Não foi possível cadastrar o boletim.");

		return false;
	}

	@Override
	public Object getObjeto(){

		//Verifica se todos os campos foram preenchidos
		if(verificaInformacoesMinimas()){

			//Captura os campos do boletim
			Boletimtratamento bol = new Boletimtratamento();
			bol.setCoordenadorRegional(etCoReg.getText().toString());
			bol.setGrs(etGrsNome.getText().toString());
			bol.setIdMunicipio(municipio.getIdMunicipio());
			bol.setIdLocalidade(localidade.getIdLocalidade());
			bol.setSemanaEpid(etSemEpidNum.getText().toString() + "/" + etSemEpidAno.getText().toString());
			bol.setNumeroAtividade(etNAtivNum.getText().toString() + "/" + etNAtivAno.getText().toString());
			bol.setData(Datas.getDate(etData.getText().toString(), "dd/MM/yyyy"));
			bol.setTurma(etTurma.getText().toString());
			bol.setIdAgente(Integer.parseInt(etAgeId.getText().toString()));
			bol.setCategoria(spCat.getSelectedItem().toString());
			bol.setTipoAtividade(spTipAtv.getSelectedItem().toString());
			bol.setAtivo(true);
			
			if(etData.getText().toString().length() == 0){
				//Preenche a data
				etData.setText(Datas.getText(Datas.getDataHoje(), "dd/MM/yyyy"));
			}

			//Verifica a data capturada
			if(bol.getData() == null){
				return null;
			}

			//Captura o Id
			if(etId.getText().toString().length() != 0){
				bol.setIdBoletimTratamento(Integer.parseInt(etId.getText().toString()));

			}

			return bol;
		}

		return null;
	}
	

}
