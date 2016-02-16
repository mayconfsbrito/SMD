package ufop.smd.gui.activity;

import java.util.StringTokenizer;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.controle.utils.Datas;
import ufop.smd.modelo.Agente;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

/**
 * Activity de visualização de informaçães de um boletim de pesquisa
 * 
 * @author maycon
 * 
 */
public class BoletimPesquisaVisualizar extends ActivityPaiBoletim {

	private Spinner spLi;

	@Override
	public void onCreate(Bundle saved) {
		super.onCreate(saved);

		setContentView(R.layout.boletim_pesquisa_visualizacao);

		inicializaComponentes();

	}

	@Override
	public boolean inicializaComponentes() {
		spLi = (Spinner) findViewById(R.id.spLi);
		super.inicializaComponentes();

		return true;
	}

	@Override
	public boolean addListeners() {
		super.addListeners();

		spTipAtv.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (spTipAtv.getSelectedItem().toString().equals("L.I.")) {
					spLi.setEnabled(true);

				} else {
					spLi.setEnabled(false);
					spLi.setSelection(0);
				}
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				spLi.setEnabled(false);
				spLi.setSelection(0);
				
			}
		});
		
		return true;
	}

	@Override
	public boolean getParcel() {

		Boletimpesquisa bol = null;

		Intent it = getIntent();
		if (it != null && it.getExtras() != null) {
			Log.d("smd", "Buscando parcel de Boletim de pesquisa.");
			bol = (Boletimpesquisa) it.getExtras().getParcelable("boletim");

		}

		preencheGui(bol);

		return true;
	}

	@Override
	public boolean preencheGui(Object obj) {

		Boletimpesquisa bol = (Boletimpesquisa) obj;

		if (bol != null) {
			// Busca os municipios, localidades e agentes relacionados a este
			// boletim
			UtilsDAO uDao = new UtilsDAO(this);
			municipio = (Municipio) uDao.getObject(Municipio.class,
					bol.getIdMunicipio(), null);
			localidade = (Localidade) uDao.getObject(Localidade.class,
					bol.getIdLocalidade(), null);
			Agente age = (Agente) uDao.getObject(Agente.class,
					bol.getIdAgente(), null);

			etCoReg.setText(bol.getCoordenadorRegional());
			etGrsNome.setText(bol.getGrs());
			etMunCod.setText(municipio.getCodigo());
			etMunNome.setText(municipio.getNome());
			etLocCod.setText(localidade.getCodigo().toString());
			etLocNome.setText(localidade.getNome());
			etAgeId.setText(age.getIdAgente().toString());
			etAgeNome.setText(age.getNome());

			StringTokenizer tok1 = new StringTokenizer(
					bol.getNumeroAtividade(), "/");
			etNAtivNum.setText(tok1.nextToken());
			etNAtivAno.setText(tok1.nextToken("").replaceAll("/", ""));

			StringTokenizer tok2 = new StringTokenizer(bol.getSemanaEpid(), "/");
			etSemEpidNum.setText(tok2.nextToken());
			etSemEpidAno.setText(tok2.nextToken("").replaceAll("/", ""));

			etId.setText(bol.getIdBoletimPesquisa().toString());
			etData.setText(Datas.getText(bol.getData(), "dd/MM/yyyy"));
			etTurma.setText(bol.getTurma());
			Componentes.setSelectedItemOnSpinner(spCat, bol.getCategoria()
					.toString());
			Componentes.setSelectedItemOnSpinner(spTipAtv,
					bol.getTipoAtividade());
			Componentes
					.setSelectedItemOnSpinner(spLi, bol.getLiAmostra() + "%");

			guiAtiva(true);

		} else {

			// Busca o usuário/agente logado na memória
			// Verifica se existem um agente na memória
			if (Login.getAgente() != null) {
				etAgeId.setText(Login.getAgente().getIdAgente().toString());
				etAgeNome.setText(Login.getAgente().getNome());

				// Caso não exista um agente na memória
			} else {
				// Sai do sistema
				logoff();
				toast("Nã existem nenhum usuário logado.");
			}

			// Preenche a data
			etData.setText(Datas.getText(Datas.getDataHoje(), "dd/MM/yyyy"));
		}

		return true;
	}

	public boolean limparGui() {
		Componentes.setSelectedItemOnSpinner(spLi, "");
		super.limparGui();

		return true;
	}

	@Override
	public boolean visitas() {

		// Verifica se o boletim já foi cadastrado
		if (etId.getText().toString().length() > 0) {

			Log.d("smd", "Parcelando em Boletim.");
			// Caso o boletim esteja cadastrado, inicia a Activity de Visitas
			Intent it = new Intent(this, VisitaPesquisaListagem.class);
			it.putExtra("idBoletim",
					Integer.parseInt(etId.getText().toString()));
			startActivityForResult(it, Constantes.TELA_VISITA_PESQUISA_LISTAGEM);

			return true;

		} else {
			// Caso o boletim não tenha sido cadastrado
			toast("O boletim deve ser cadastrado antes de visualizar visitas.");
			return false;
		}
	}

	@Override
	public boolean cadastrarAlterar() {

		// Captura o objeto na gui
		Boletimpesquisa bol = (Boletimpesquisa) getObjeto();

		// Verifica se a interface está completa
		if (bol != null) {

			// Verifica se é para cadastrar ou alterar, conferindo se o id está
			// preenchido ou não.
			if (bol.getIdBoletimPesquisa() == null) {

				// Cadastra
				long id = dao.cadastrar("Boletimpesquisa",
						bol.getContentValues());
				if (id != 0) {
					toast("Boletim de pesquisa cadastrado com sucesso.");

					etId.setText(id + "");
					//finish();

				} else {
					toast("Erro ao cadastrar o boletim.");
				}

			} else {
				// Altera
				if (dao.alterar("Boletimpesquisa", bol.getContentValues(),
						"idBoletimPesquisa=?", new String[] { bol
								.getIdBoletimPesquisa().toString() })) {
					toast("Boletim alterado com sucesso");
					//finish();

				} else {
					toast("Erro ao alterar o boletim.");

				}

			}

			return true;

		}

		toast("Nã foi possível cadastrar o boletim.");

		return false;
	}

	@Override
	public Object getObjeto() {

		// Verifica se todos os campos foram preenchidos
		if (verificaInformacoesMinimas()) {

			// Captura os campos do boletim
			Boletimpesquisa bol = new Boletimpesquisa();
			bol.setCoordenadorRegional(etCoReg.getText().toString());
			bol.setGrs(etGrsNome.getText().toString());
			bol.setIdMunicipio(municipio.getIdMunicipio());
			bol.setIdLocalidade(localidade.getIdLocalidade());
			bol.setSemanaEpid(etSemEpidNum.getText().toString() + "/"
					+ etSemEpidAno.getText().toString());
			bol.setNumeroAtividade(etNAtivNum.getText().toString() + "/"
					+ etNAtivAno.getText().toString());
			bol.setData(Datas
					.getDate(etData.getText().toString(), "dd/MM/yyyy"));
			bol.setTurma(etTurma.getText().toString());
			bol.setIdAgente(Integer.parseInt(etAgeId.getText().toString()));
			bol.setCategoria(spCat.getSelectedItem().toString());
			bol.setTipoAtividade(spTipAtv.getSelectedItem().toString());
			if(spTipAtv.getSelectedItem().toString().equals("L.I.")){
				bol.setLiAmostra(Integer.parseInt(spLi.getSelectedItem().toString().replaceAll("%", "")));
			} 
			bol.setAtivo(true);

			if (etData.getText().toString().length() == 0) {
				// Preenche a data
				etData.setText(Datas.getText(Datas.getDataHoje(), "dd/MM/yyyy"));
			}

			// Verifica a data capturada
			if (bol.getData() == null) {
				return null;
			}

			// Captura o Id
			if (etId.getText().toString().length() != 0) {
				bol.setIdBoletimPesquisa(Integer.parseInt(etId.getText()
						.toString()));

			}

			return bol;
		}

		return null;
	}

	@Override
	public boolean verificaInformacoesMinimas(){
		
		if(!super.verificaInformacoesMinimas()){
			return false;
		}
		
		if(spTipAtv.getSelectedItem().toString().equals("L.I.")){
			if (!verificaPreenchido("LiAmostra não preenchida.", spLi)){
				return false;
			}
		}
		
		return true;
		
	}

}
