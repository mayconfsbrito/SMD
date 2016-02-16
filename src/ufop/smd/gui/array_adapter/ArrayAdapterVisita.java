package ufop.smd.gui.array_adapter;

import java.util.List;

import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Visitapesquisa;
import ufop.smd.modelo.Visitatratamento;
import ufop.smd.persistencia.dao.UtilsDAO;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrayAdapterVisita extends ArrayAdapter {

	private int resource;
	private Context context;

	public ArrayAdapterVisita(Context context, int textViewResourceId, List object) {
		super(context, textViewResourceId, object);
		this.resource = textViewResourceId;
		this.context = context;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {

		LinearLayout vView;

		int idLogradouro = 0;
		int idImovel = 0;
		String hora = null;
		
		if(getItem(pos) instanceof Visitatratamento){
			Visitatratamento visita = (Visitatratamento) getItem(pos);
			idLogradouro = visita.getIdLogradouro();
			idImovel = visita.getIdImovel();
			hora = visita.getHora().toString();
			
		} else {
			Visitapesquisa visita = (Visitapesquisa) getItem(pos);
			idLogradouro = visita.getIdLogradouro();
			idImovel = visita.getIdImovel();
			hora = visita.getHora().toString();
			
		}

		// Infla a view se a mesma vier como nula
		if (view == null) {
			vView = new LinearLayout(getContext());

			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(resource, vView, true);

		} else {

			vView = (LinearLayout) view;

		}

		//Busca as informações da visita
		UtilsDAO uDao = new UtilsDAO(context);
		Logradouro log = (Logradouro) uDao.getObject(Logradouro.class, idLogradouro, null);
		Imovel imo = (Imovel) uDao.getObject(Imovel.class, idImovel, null);

		//Preenche os compenentes da gui
		TextView tv1 = (TextView) vView.findViewById(android.R.id.text1);
		TextView tv2 = (TextView) vView.findViewById(android.R.id.text2);
		tv1.setText(hora);
		String str = log.getNome() + "\nNº" + imo.getNumero();
		if(imo.getComplemento() != null && !imo.getComplemento().equals("")){
			str +=  " - " + imo.getComplemento();
		}
		tv2.setText(str);

		return vView;

	}
}
