package ufop.smd.gui.array_adapter;

import java.util.List;

import ufop.smd.controle.utils.Datas;
import ufop.smd.modelo.Boletimpesquisa;
import ufop.smd.modelo.Boletimtratamento;
import ufop.smd.modelo.Localidade;
import ufop.smd.modelo.Municipio;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrayAdapterBoletim extends ArrayAdapter {
	
	private int resource;
	private Context context;

	@SuppressWarnings("unchecked")
	public ArrayAdapterBoletim(Context context, int textViewResourceId, List object) {
		super(context, textViewResourceId, object);
		this.resource = textViewResourceId;
		this.context = context;
	}
	
	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		
		LinearLayout bolView;
		int idMunicipio = 0;
		int idLocalidade = 0;
		String data = null;
		String nome = null;
		
		if(getItem(pos) instanceof Boletimtratamento){
			
			Boletimtratamento bol = (Boletimtratamento) getItem(pos);
						
			idMunicipio = bol.getIdMunicipio();
			idLocalidade = bol.getIdLocalidade();
			UtilsDAO uDao = new UtilsDAO(context);
			Municipio mun = (Municipio) uDao.getObject(Municipio.class, idMunicipio, null);
			Localidade loc = (Localidade) uDao.getObject(Localidade.class, idLocalidade, null);
			
			data = Datas.getText(bol.getData(), "dd/MM/yy");
			nome = mun.getNome() + "\n" + loc.getNome();
			
		} else {
			Boletimpesquisa bol = (Boletimpesquisa) getItem(pos);
			
			idMunicipio = bol.getIdMunicipio();
			idLocalidade = bol.getIdLocalidade();
			UtilsDAO uDao = new UtilsDAO(context);
			Municipio mun = (Municipio) uDao.getObject(Municipio.class, idMunicipio, null);
			Localidade loc = (Localidade) uDao.getObject(Localidade.class, idLocalidade, null);
			
			data = Datas.getText(bol.getData(), "dd/MM/yy");
			nome = mun.getNome() + "\n" + loc.getNome();
			
		}
		
		// Infla a view se a mesma vier como nula
		if (view == null) {
			bolView = new LinearLayout(getContext());

			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(resource, bolView, true);

		} else {
			bolView = (LinearLayout) view;
			
		}

		//Preenche os compenentes da gui
		TextView tv1 = (TextView) bolView.findViewById(android.R.id.text1);
		TextView tv2 = (TextView) bolView.findViewById(android.R.id.text2);
		tv1.setText(data);
		tv2.setText(nome);
		
		return bolView;

	}

}
