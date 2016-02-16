package ufop.smd.gui.activity;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Horas;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import ufop.smd.modelo.Visitatratamento;
import ufop.smd.persistencia.dao.UtilsDAO;
import ufop.smd.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Activity de visualização de informaçães de um boletim de tratamento
 * @author maycon
 *
 */
public class VisitaTratamentoVisualizar extends ActivityPaiVisita {

	//Componentes da Activity
	protected Spinner spAdul;
	protected Spinner spLarv;
	protected Spinner spPend;
	protected EditText etDepTra;
	

	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);

		setContentView(R.layout.visita_tratamento);

		inicializaComponentes();
	}

	@Override
	public boolean inicializaComponentes() {

		//Inicializa todos os componentes da activity
		etQua = (EditText)findViewById(R.id.etQua);
		etLog = (EditText)findViewById(R.id.etLog);
		etImo = (EditText)findViewById(R.id.etImo);
		spAdul = (Spinner)findViewById(R.id.spAdul);
		spLarv = (Spinner)findViewById(R.id.spLarv);
		spPend = (Spinner)findViewById(R.id.spPend);
		spTipUn = (Spinner)findViewById(R.id.spTipUn);
		etDepTra = (EditText)findViewById(R.id.etDepTra);
		cbUltVisBol = (CheckBox)findViewById(R.id.cbUltVis);
		cbQC = (CheckBox)findViewById(R.id.cbQC);
		etIdVis = (EditText)findViewById(R.id.etIdVis);
		etIdBol = (EditText)findViewById(R.id.etIdBol);
		etHora = (EditText)findViewById(R.id.etHora);
		btQua = (Button)findViewById(R.id.btQua);
		btLog = (Button)findViewById(R.id.btLog);
		btImo = (Button)findViewById(R.id.btImo);
		btCadAlt = (Button)findViewById(R.id.btCadAlt);
		btLimpar = (Button)findViewById(R.id.btLimpar);

		limparGui();
		addListeners();

		//Captura os elementos do boletim
		getParcel();
		localidade = getLocalidadeBoletim();
		municipio = (Municipio) uDao.getObject(Municipio.class, localidade.getIdMunicipio(), null);

		//Preenche o spinner de quadras de acordo com a localidade do boletim
		preencheGui();

		return true;
	}
	
	@Override
	public boolean addListeners() {
		super.addListeners();

		spPend.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (spPend.getSelectedItem().toString().equals("Recusada") 
						|| spPend.getSelectedItem().toString().equals("Fechada")) {
					
					spAdul.setEnabled(false);
					spLarv.setEnabled(false);
					etDepTra.setEnabled(false);
					spAdul.setSelection(0);
					spLarv.setSelection(0);
					etDepTra.setText("0");

				} else {
					spAdul.setEnabled(true);
					spLarv.setEnabled(true);
					etDepTra.setEnabled(true);
					//etDepTra.setText("");
					
				}
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				spAdul.setEnabled(true);
				spLarv.setEnabled(true);
				etDepTra.setEnabled(true);
				etDepTra.setText("");
				
			}
		});
		
		return true;
	}
	
	@Override
	public boolean getParcel(){

		Log.d("smd", "Captura parcel em visita de tratamento.");
		bolTra = null;
		visTra = null;

		Intent it = getIntent();
		if(it != null && it.getExtras() != null){
			idBoletim = it.getExtras().getInt("idBoletim");
			etIdBol.setText(idBoletim + "");
			bolTra = (Boletimtratamento) new UtilsDAO(this).getObject(Boletimtratamento.class, idBoletim, null);
			bolPes = null;

			if(it.getExtras().getParcelable("visita") != null){
				visTra = (Visitatratamento)it.getExtras().getParcelable("visita");
				visPes = null;
			} 
		}

		return true;
	}

	@Override
	public boolean preencheGui(){

		if(visTra != null){

			//Busca os objetos relacionados a @obj
			UtilsDAO uDao = new UtilsDAO(this);

			//Preenche os Spinners e busca os objetos relacionados a eles
			quadra = (Quadra) uDao.getObject(Quadra.class, visTra.getIdQuadra(), null);
			logradouro = (Logradouro) uDao.getObject(Logradouro.class, visTra.getIdLogradouro(), null);
			imovel = (Imovel) uDao.getObject(Imovel.class, visTra.getIdImovel(), null);

			//Preenche os componentes do layout
			etQua.setText(quadra.getCodigo().toString());
			etLog.setText(logradouro.getNome());
			String strImo = imovel.getNumero() + " - ";
			if(imovel.getComplemento() != null && !imovel.getComplemento().equals("")){
				strImo +=  imovel.getComplemento();
			}
			etImo.setText(strImo);
			setAdulticida(visTra);
			setLarvicida(visTra);
			setPendencias(visTra);
			Componentes.setSelectedItemOnSpinner(spTipUn, visTra.getTipoUnidade());
			etIdVis.setText(visTra.getIdVisitaTratamento().toString());
			etIdBol.setText(visTra.getIdBoletimtratamento().toString());
			etHora.setText(visTra.getHora().toString().toString());
			cbUltVisBol.setChecked(visTra.isUltimaVisitaBoletim());
			Log.d("ufop.smd","visTra=" + visTra.isQuadraConcluida());
			cbQC.setChecked(visTra.isQuadraConcluida());
			etDepTra.setText(visTra.getDepositosTratados().toString());

			guiAtiva(true);

		} else {

			//Busca o usuário/agente logado na memória
			//Verifica se existem um agente na memória
			if(Login.getAgente() == null){
				//Sai do sistema
				logoff();
				toast("Não existem nenhum usuário logado.");

				//Caso não exista um agente na memória
			}

			//Preenche demais campos
			etHora.setText(Horas.getHoraAgora().toString());
			etIdBol.setText(idBoletim.toString());

		}

		return true;
	}

	@Override
	public boolean limparGui(){

		Componentes.apagaSpinner(spAdul, spLarv, spPend, spTipUn);
		Componentes.apagaEditText(etDepTra, etQua, etLog, etImo);
		Componentes.disabledJComponent(etQua, etLog, etImo);

		municipio = null;
		localidade = null;
		logradouro = null;
		imovel = null;

		guiAtiva(false);

		return true;
	}

	@Override
	public boolean cadastrarAlterar(){

		if(getObjeto() != null){
			//Captura o objeto na gui
			Visitatratamento vis = (Visitatratamento) getObjeto();
			Log.d("ufop.smd","getObjeto=" + vis.isQuadraConcluida());
			
			
			//Verifica se a quadra foi concluída em outra visita
			stopCursor(cursor);
			cursor = dao.consultar("Visitatratamento", null, 
					"idBoletimTratamento=? AND quadraConcluida=?", 
					new String[]{idBoletim.toString(), "1"}, null, null, "hora");
			if(this.verificaQuadraConcluida(cursor, vis.getIdQuadra(), this.getIdGui(), "Visitatratamento")){
				toast("Esta quadra já foi concluída neste boletim.");
				return false;
			}

			//Verifica se é para cadastrar ou alterar, conferindo se o id está preenchido ou não.
			if(vis.getIdVisitaTratamento() == null){

				//Cadastra
				long id = dao.cadastrar("Visitatratamento", vis.getContentValues());
				if(id != 0){
					toast("Visita de tratamento cadastrado com sucesso.");

					etIdVis.setText(id + "");
					etHora.setText(vis.getHora().toString());
					//finish();

				} else {
					toast("Erro ao cadastrar a visita.");
				}

			} else {
				//Altera
				if(dao.alterar("Visitatratamento", vis.getContentValues(), "idVisitatratamento=?", new String[]{vis.getIdVisitaTratamento().toString()})){
					toast("Visita alterada com sucesso");
					//finish();

				} else {
					toast("Erro ao alterar a visita.");

				}

			}

			return true;
		}

		toast("Não foi possível cadastrar a visita.");

		return false;
	}

	public Object getObjeto(){

		//Verifica se todos os campos foram preenchidos
		if(verificaInformacoesMinimas()){

			//Captura os campos do boletim
			//Adulticida
			int adul = -1;
			if (spAdul.getSelectedItem().toString().equals("Novo Pacote")) {
				adul = Constantes.NOVO_PACOTE;

			} else if (spAdul.getSelectedItem().toString().equals("Pacote Usado")) {
				adul = Constantes.PACOTE_USADO;

			} else {
				adul = Constantes.NAO_USADO;
			}

			//Larvicida
			int larv = -1;
			if (spLarv.getSelectedItem().toString().equals("Novo Pacote")) {
				larv = Constantes.NOVO_PACOTE;

			} else if (spLarv.getSelectedItem().toString().equals("Pacote Usado")) {
				larv = Constantes.PACOTE_USADO;

			} else {
				larv = Constantes.NAO_USADO;
			}

			Visitatratamento vis = new Visitatratamento();
			vis.setDepositosTratados(Integer.parseInt(etDepTra.getText().toString()));
			vis.setHora(Horas.getTime(etHora.getText().toString()));
			vis.setIdBoletimtratamento(Integer.parseInt(etIdBol.getText().toString()));
			vis.setIdImovel(imovel.getIdImovel());
			vis.setIdLogradouro(logradouro.getIdLogradouro());
			vis.setIdQuadra(quadra.getIdQuadra());
			vis.setInseticidaAdulticida(adul);
			vis.setInseticidaLarvicida(larv);
			definePendencia(spPend, vis);
			vis.setTipoUnidade(spTipUn.getSelectedItem().toString());
			vis.setUltimaVisitaBoletim(cbUltVisBol.isChecked());
			vis.setQuadraConcluida(cbQC.isChecked());

			//Define a Hora
			if (etHora.getText().toString().length() == 0) {
				vis.setHora(Horas.getHoraAgora());
			} else {
				vis.setHora(Horas.getTime(etHora.getText().toString()));
			}

			//Verifica a data capturada
			if(vis.getHora() == null){
				return null;
			}

			//Captura o Id
			if(etIdVis.getText().toString().length() != 0){
				vis.setIdVisitaTratamento(Integer.parseInt(etIdVis.getText().toString()));

			}

			return vis;
		}

		return null;
	}

	@Override
	public boolean verificaInformacoesMinimas(){

		if(!super.verificaInformacoesMinimas()){
			return false;
		}

		if(!(spPend.getSelectedItem().equals("Fechada")
				|| spPend.getSelectedItem().equals("Recusada"))){
			if (!verificaPreenchido("Adulticida não definido.", spAdul)){
				return false;
			}
	
			if (!verificaPreenchido("Larvicida não definido", spLarv)){
				return false;
			}
		}

		if (!verificaPreenchido("Pendência não definida.", spPend)){
			return false;
		}

		if (!verificaPreenchido("Depósitos tratados não definido.", etDepTra)){
			return false;
		}

		return true;
	}
	
	/**
	 * Define a pendência
	 */
	public boolean definePendencia(Spinner sp, Visitatratamento vis){

		String str = sp.getSelectedItem().toString();

		if(str.equals("Nenhuma")) {
			vis.setPendenciaFech(false);
			vis.setPendenciaRec(false);
			vis.setPendenciaResg(false);

		} else 	if(str.equals("Resgatada")){
			vis.setPendenciaFech(false);
			vis.setPendenciaRec(false);
			vis.setPendenciaResg(true);

		} else if(str.equals("Fechada")){
			vis.setPendenciaFech(true);
			vis.setPendenciaRec(false);
			vis.setPendenciaResg(false);

		} else {
			vis.setPendenciaFech(false);
			vis.setPendenciaRec(true);
			vis.setPendenciaResg(false);

		}

		return true;

	}

	/**
	 * Escolhe uma opção no Spinner de Adulticida 
	 */
	public void setAdulticida(Visitatratamento vis){

		if(vis.getInseticidaAdulticida() == Constantes.NAO_USADO){
			Componentes.setSelectedItemOnSpinner(spAdul, "Não Usado");

		} else if (vis.getInseticidaAdulticida() == Constantes.NOVO_PACOTE){
			Componentes.setSelectedItemOnSpinner(spAdul, "Novo Pacote");

		} else {
			Componentes.setSelectedItemOnSpinner(spAdul, "Pacote Usado");

		}

	}

	/**
	 * Escolhe uma opção no Spinner de Larvicida 
	 */
	public void setLarvicida(Visitatratamento vis){

		if(vis.getInseticidaLarvicida() == Constantes.NAO_USADO){
			Componentes.setSelectedItemOnSpinner(spLarv, "Não Usado");

		} else if (vis.getInseticidaLarvicida() == Constantes.NOVO_PACOTE){
			Componentes.setSelectedItemOnSpinner(spLarv, "Novo Pacote");

		} else {
			Componentes.setSelectedItemOnSpinner(spLarv, "Pacote Usado");

		}
	}

	/**
	 * Escolhe uma opção no spinner de Pendências
	 */
	public void setPendencias(Visitatratamento vis){

		if(vis.getPendenciaFech()){
			Componentes.setSelectedItemOnSpinner(spPend, "Fechada");

		} else if(vis.getPendenciaRec()){
			Componentes.setSelectedItemOnSpinner(spPend, "Recusada");

		} else if(vis.getPendenciaResg()){
			Componentes.setSelectedItemOnSpinner(spPend, "Resgatada");

		} else {
			Componentes.setSelectedItemOnSpinner(spPend, "Nenhuma");
		}
	}

}
