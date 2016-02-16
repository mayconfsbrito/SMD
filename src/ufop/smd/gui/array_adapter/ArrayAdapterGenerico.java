package ufop.smd.gui.array_adapter;

import java.util.List;

import ufop.smd.modelo.Imovel;
import ufop.smd.modelo.Logradouro;
import ufop.smd.modelo.Quadra;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrayAdapterGenerico extends ArrayAdapter<Object> {
	
	private int resource;
	private Context context;

	public ArrayAdapterGenerico(Context context, int textViewResourceId, List<Object> object) {
		super(context, textViewResourceId, object);
		this.resource = textViewResourceId;
		this.context = context;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		
		LinearLayout vView;
		

		// Infla a view se a mesma vier como nula
		if (view == null) {
			vView = new LinearLayout(getContext());

			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(resource, vView, true);

		} else {
			vView = (LinearLayout) view;
			
		}
		
		TextView tv1 = (TextView) vView.findViewById(android.R.id.text1);
		TextView tv2 = (TextView) vView.findViewById(android.R.id.text2);
		
		//Verifica se o objeto é uma quadra
		if(getItem(pos) instanceof Quadra){
			
			Quadra qua = (Quadra) getItem(pos);
			tv1.setText(qua.getCodigo().toString());
			
			//Verifica se o objeto é um Logradouro	
		} else if(getItem(pos) instanceof Logradouro){
			
			Logradouro log = (Logradouro) getItem(pos);
			tv1.setText(log.getNome());
			
		} else if(getItem(pos) instanceof Imovel){
			
			Imovel imo = (Imovel) getItem(pos);
			String str = "Nº " + imo.getNumero();
			if(imo.getComplemento() != null && !imo.getComplemento().equals("")){
				str +=  " - " + imo.getComplemento();
			}
			tv1.setText(str);
			
		}
		
	
		
		return vView;

	}
}
