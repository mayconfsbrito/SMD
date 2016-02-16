package ufop.smd.gui.activity;

import java.util.Iterator;

import ufop.smd.controle.Constantes;
import ufop.smd.controle.utils.Componentes;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Municipio;
import ufop.smd.modelo.Quadra;
import ufop.smd.modelo.Visitapesquisa;
import ufop.smd.modelo.Visitatratamento;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Superclasse para activitys de visualização/cadastro/alteração de visitas de boletins
 * @author maycon
 *
 */
public abstract class ActivityPaiVisita extends ActivityPai {

	//Elementos na memória da Activity
	Integer idBoletim = null;
	Boletimtratamento bolTra = null;
	Boletimpesquisa bolPes = null;
	Visitatratamento visTra = null;
	Visitapesquisa visPes = null;
	Municipio municipio = null;
	Localidade localidade = null;
	Quadra quadra = null;
	Logradouro logradouro = null;
	Imovel imovel = null;
	UtilsDAO uDao = new UtilsDAO(this);

	//Componentes da Activity
	protected EditText etQua;
	protected EditText etLog;
	protected EditText etImo;
	protected Spinner spTipUn;
	protected CheckBox cbUltVisBol;
	protected CheckBox cbQC;
	protected EditText etIdVis;
	protected EditText etIdBol;
	protected EditText etHora;
	protected Button btQua;
	protected Button btLog;
	protected Button btImo;
	protected Button btCadAlt;
	protected Button btLimpar;

	/**
	 * Sempre implementar o método onCreate
	 */


	/**
	 * Reimplementado para poder capturar o resultado das buscas de Quadra, Logradouro e Imovel
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){

		switch (resultCode){
		case Constantes.LOGOFF:
			logoff();
			break;

		case Constantes.QUADRA:
			quadra = (Quadra) uDao.getObject(Quadra.class, data.getExtras().getInt("idQuadra"), null);
			etQua.setEnabled(true);
			etQua.setText(quadra.getCodigo().toString());
			logradouro = null;
			imovel = null;
			Componentes.apagaEditText(etLog, etImo);
			Componentes.disabledJComponent(etLog, etImo);

			break;

		case Constantes.LOGRADOURO:
			logradouro = (Logradouro) uDao.getObject(Logradouro.class, data.getExtras().getInt("idLogradouro"), null);
			etLog.setEnabled(true);
			etLog.setText(logradouro.getNome());
			etImo.setText("");
			etImo.setEnabled(false);
			imovel = null;

			break;

		case Constantes.IMOVEL:
			imovel = (Imovel) uDao.getObject(Imovel.class, data.getExtras().getInt("idImovel"), null);
			etImo.setEnabled(true);
			String strImo = imovel.getNumero() + " - ";
			if(imovel.getComplemento() != null && !imovel.getComplemento().equals("")){
				strImo +=  imovel.getComplemento();
			}
			etImo.setText(strImo);

			break;

		}
	}

	@Override
	public abstract boolean inicializaComponentes();

	@Override
	public boolean addListeners() {

		btQua.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				buscarQuadra();
			}
		});


		btLog.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				buscarLogradouro(quadra);
			}
		});

		btImo.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				buscarImovel(quadra, logradouro);
			}
		});

		btCadAlt.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				cadastrarAlterar();
			}
		});

		btLimpar.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				limparGui();
			}
		});



		return true;
	}

	/**
	 * Captura o objeto enviado pela activity anterior através de parcelable 
	 * @return
	 */
	public abstract boolean getParcel();

	/**
	 * Preenche os componentes da Activity de acordo com o objeto enviado por parâmetro
	 * @return
	 */
	public abstract boolean preencheGui();

	/**
	 * Seta os componentes da Gui de acordo como o estado de @ativa
	 * @param ativa
	 * @return
	 */
	public boolean guiAtiva(boolean ativa){

		if(ativa){

			Componentes.enabledJComponent(btCadAlt, etImo, etQua, etLog);

		} else {

			Componentes.disabledJComponent(etImo, etQua, etLog);

		}

		return true;
	}

	/**
	 * Limpa os componentes da activity
	 * @return
	 */
	public abstract boolean limparGui();

	/**
	 * Cadastra ou altera o objeto visualizado na activity
	 */
	public abstract boolean cadastrarAlterar();

	/**
	 * Captura as informaçães inseridas nos componentes da Activity e instancia um novo objeto
	 * @return
	 */
	public abstract Object getObjeto();

	/**
	 * Verifica se foram preenchidas todas as informaçães mínimas para cadastro/alteração do boletim 
	 * @return
	 */
	public boolean verificaInformacoesMinimas(){

		if (!verificaPreenchido("Quadra não definida.", etQua) || quadra == null){
			return false;
		}

		if (!verificaPreenchido("Logradouro não definido", etLog) || logradouro == null){
			return false;
		}

		if (!verificaPreenchido("Imóvel não definido.", etImo) || imovel == null){
			return false;
		}

		if (!verificaPreenchido("Tipo de unidade não definida.", spTipUn)){
			return false;
		}

		if (!verificaPreenchido("Id do boletim não definido.", etIdBol)){
			return false;
		}

		return true;
	}
	
	/**
     * Verifica se a quadra consultada já foi concluida no boletim atual em outra visita
     * @return false - a quadra não foi concluída 
     * @return true - a quadra foi concluída
     */
    protected boolean verificaQuadraConcluida(Cursor cursor, Integer idQuadra, Integer idVisitaAtual, String nomeClasse) {

    	while(cursor != null && !cursor.isAfterLast()){
            if (nomeClasse.equals("Visitapesquisa")) {
                Visitapesquisa vis = new Visitapesquisa(cursor);
                if (vis.isQuadraConcluida()
                        && (vis.getIdQuadra().equals(idQuadra))) {
                    if (idVisitaAtual != null && vis.getIdVisitaPesquisa() != null && 
                            !idVisitaAtual.equals(vis.getIdVisitaPesquisa())) {
                        return true;
                    } else if(idVisitaAtual == null || vis.getIdVisitaPesquisa() == null){
                        return true;
                    }
                }

            } else if (nomeClasse.equals("Visitatratamento")) {
                Visitatratamento vis = new Visitatratamento(cursor);
                if (vis.isQuadraConcluida()
                        && (vis.getIdQuadra().equals(idQuadra))) {
                    if (idVisitaAtual != null && vis.getIdVisitaTratamento() != null && 
                            !idVisitaAtual.equals(vis.getIdVisitaTratamento())) {
                        return true;
                    } else if(idVisitaAtual == null || vis.getIdVisitaTratamento() == null){
                        return true;
                    }
                }
            }
        
            cursor.moveToNext();
        }
    	
    	return false;
    }

	/**
	 * Abre a activity de busca para aguardar o retorno de uma Quadra selecionada na mesma
	 * @return
	 */
	public void buscarQuadra(){

		Intent it = new Intent(this, ListagemGenerica.class);
		it.putExtra("nomeTabela", "Quadra");
		it.putExtra("selecao", "idLocalidade=?");
		it.putExtra("idLocalidade", localidade.getIdLocalidade());
		startActivityForResult(it, Constantes.TELA_BUSCA);

	}

	/**
	 * Abre a activity de busca para aguardar o retorno de um Logradouro selecionado na mesma
	 */
	public void buscarLogradouro(Quadra quadra){

		if(quadra != null){
			Intent it = new Intent(this, ListagemGenerica.class);
			it.putExtra("nomeTabela", "Logradouro");
			it.putExtra("idQuadra", quadra.getIdQuadra());
			startActivityForResult(it, Constantes.TELA_BUSCA);

		} else {
			toast("Defina uma quadra.");
		}

	}

	/**
	 * Abre a activity de busca para aguardar o retorno de um Imovel selecionado na mesma
	 */
	public void buscarImovel(Quadra quadra, Logradouro logradouro){

		if(quadra != null && logradouro != null){
			Intent it = new Intent(this, ListagemGenerica.class);
			it.putExtra("nomeTabela", "Imovel");
			it.putExtra("selecao", "idQuadra=? AND idLogradouro=?");
			it.putExtra("idQuadra", quadra.getIdQuadra());
			it.putExtra("idLogradouro", logradouro.getIdLogradouro());
			startActivityForResult(it, Constantes.TELA_BUSCA);
			
		} else {
			toast("Defina a quadra e o logradouro.");
		}

	}

	/**
	 * Captura a localidade a partir do id do Boletim
	 */
	public Localidade getLocalidadeBoletim(){

		Integer idLocalidade = null;


		if(bolTra != null){
			idLocalidade = bolTra.getIdLocalidade();

		} else if(bolPes != null){
			idLocalidade = bolPes.getIdLocalidade();
		}

		if(idLocalidade != null){
			return (Localidade) uDao.getObject(Localidade.class, idLocalidade, null);
		} else {
			return null;
		}

	}
	
	/**
	 * Retorna o id da visita registrado na gui
	 * @return
	 */
	public Integer getIdGui(){
		if(etIdVis != null){
			if(etIdVis.getText().length() > 0){
				return(Integer.parseInt(etIdVis.getText().toString()));
			}
		}
		
		return null;
	}

}
