package ufop.smd.gui.activity;

import java.util.ArrayList;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.gui.array_adapter.ArrayAdapterGenerico;
import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Quadra;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ListagemGenerica extends ActivityPaiList {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		Log.d("smd", "Listando busca.");
		setContentView(R.layout.listagem_generica_titulo);

		inicializaComponentes();
	}

	@Override
	public boolean inicializaComponentes() {

		Intent it = getIntent();
		String nomeTabela = it.getExtras().getString("nomeTabela");
		TextView tvTitulo = (TextView)findViewById(R.id.tvTitulo);
		tvTitulo.setText(nomeTabela);

		stopCursor(cursor);

		//Caso a busca seja de Quadra
		if(nomeTabela.equals("Quadra")){
			cursor = dao.consultar(nomeTabela, null, it.getExtras().getString("selecao"), new String[]{it.getExtras().getInt("idLocalidade") + ""}, null, null, "codigo");

			//Caso a busca seja de Logradouro
		} else if (nomeTabela.equals("Logradouro")){
			cursor = dao.consultar("SELECT l.idLogradouro, l.idMunicipio, l.nome " +
					"FROM Logradouro l, Quadra q, Quadralogradouro ql " +
					"WHERE l.idLogradouro=ql.idLogradouro AND q.idQuadra=ql.idQuadra AND q.idQuadra=? order by l.nome", 
					new String[]{it.getExtras().getInt("idQuadra") + ""});


			//Caso a busca seja de Imovel
		} else if (nomeTabela.equals("Imovel")){
			cursor = dao.consultar(nomeTabela, null, it.getExtras().getString("selecao"), new String[]{it.getExtras().getInt("idQuadra") + "" , it.getExtras().getInt("idLogradouro")  + ""}, null, null, "numero, complemento");

		}

		//Cria uma lista com os boletins
		ArrayList<Object> list = new ArrayList<Object>();
		if(cursor != null && !cursor.isAfterLast()){
			while(cursor != null && !cursor.isAfterLast()){
				if(nomeTabela.equals("Quadra")){
					list.add(new Quadra(cursor));

				} else if (nomeTabela.equals("Logradouro")){
					list.add(new Logradouro(cursor));

				} else if (nomeTabela.equals("Imovel")){
					list.add(new Imovel(cursor));
				}

				cursor.moveToNext();
			}
		} else {
			if(nomeTabela.equals("Quadra")){
				toast("Nenhuma Quadra cadastrada para esta Localidade");

			} else if (nomeTabela.equals("Logradouro")){
				toast("Nenhum Logradouro cadastrado para esta Quadra");

			} else if (nomeTabela.equals("Imovel")){
				toast("Nenhum Imóvel cadastrado para esta Quadra e Logradouro");
			}
		}

		ArrayAdapterGenerico arrayAdapter = new ArrayAdapterGenerico(this, R.layout.listagem_generica, list);
		setListAdapter(arrayAdapter);

		return true;
	}

	/**
	 * Retorna o objeto selecionado para a activity anterior
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent it = new Intent();
		int constante = 0;

		if(this.getListAdapter().getItem(position) instanceof Quadra){
			it.putExtra("idQuadra", ((Quadra)this.getListAdapter().getItem(position)).getIdQuadra());
			constante = Constantes.QUADRA;

		} else if(this.getListAdapter().getItem(position) instanceof Logradouro){
			it.putExtra("idLogradouro", ((Logradouro)this.getListAdapter().getItem(position)).getIdLogradouro());
			constante = Constantes.LOGRADOURO;

		} else if(this.getListAdapter().getItem(position) instanceof Imovel){
			it.putExtra("idImovel", ((Imovel)this.getListAdapter().getItem(position)).getIdImovel());
			constante = Constantes.IMOVEL;

		}

		setResult(constante, it);
		finish();

	}

	@Override
	public boolean addListeners() {
		// TODO Auto-generated method stub
		return false;
	}

}
