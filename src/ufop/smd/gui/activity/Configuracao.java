package ufop.smd.gui.activity;

import java.util.StringTokenizer;

import ufop.smd.R;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Configuracao extends ActivityPai {

	private EditText etIp1;
	private EditText etIp2;
	private EditText etIp3;
	private EditText etIp4;
	private EditText etPorta;
	private EditText etCodMun;
	private Button btOk;
	private Button btRes;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.configuracao);
		inicializaComponentes();
	}
	
	@Override
	public boolean inicializaComponentes() {
		
		etIp1 = (EditText)findViewById(R.id.etIp1);
		etIp2 = (EditText)findViewById(R.id.etIp2);
		etIp3 = (EditText)findViewById(R.id.etIp3);
		etIp4 = (EditText)findViewById(R.id.etIp4);
		etPorta = (EditText)findViewById(R.id.etPorta);
		etCodMun = (EditText)findViewById(R.id.etCodMun);
		btOk = (Button)findViewById(R.id.btOk);
		btRes = (Button)findViewById(R.id.btRest);
		
	    //Captura no bd a configuração da apk
	    stopCursor(cursor);
	    cursor = dao.consultar("configuracao", null, null, null);
	    if(cursor != null && !cursor.isAfterLast()){
	    	etPorta.setText(cursor.getString(1));
	    	etCodMun.setText(cursor.getString(3));
	    	
	    	StringTokenizer strTok = new StringTokenizer(cursor.getString(2), ".");
	    	etIp1.setText(strTok.nextToken());
	    	etIp2.setText(strTok.nextToken());
	    	etIp3.setText(strTok.nextToken());
	    	etIp4.setText(strTok.nextToken());
	    	
	    }
	    
	    addListeners();
		
		return false;
	}

	@Override
	public boolean addListeners() {
		
		btRes.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				limparGui();
				
			}
			
		});
		
		btOk.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v){
				salvar();
			}
		});
		
		return false;
	}
	
	/**
	 * Salva no bd a configuração inserida na activity
	 */
	public boolean salvar(){
		
		if(!verificaCampos()){
			return false;
		}
		
		String ip = etIp1.getText().toString() + "." + etIp2.getText().toString() + "." + etIp3.getText().toString() + "." + etIp4.getText().toString();
		Integer porta = Integer.parseInt(etPorta.getText().toString());
		String codMun = etCodMun.getText().toString();
		
		ContentValues values = new ContentValues();
		values.put("id", 1);
		values.put("porta", porta);
		values.put("ipServidor", ip);
		values.put("codigoMunicipio", codMun);
		dao.alterar("configuracao", values, "id=?", new String[] {"1"});
		
		toast("Configuraçães salvas com sucesso.");
		finish();
				
		return true;
				
	}
	
	public void limparGui(){
		
		etPorta.setText("40500");
		etIp1.setText("192");
		etIp2.setText("168");
		etIp3.setText("1");
		etIp4.setText("100");
		etCodMun.setText("0000000");
		
	}
	
	/**
	 * Verifica as informaçães mínimas
	 * @return true se for válido, false se algum campo está inválido
	 */
	public boolean verificaCampos(){
		
		if (!verificaPreenchido("A porta deve ser preenchida.", etPorta)){
			return false;
		}
		
		if (!verificaPreenchido("O Ip deve ser preenchido corretamente.", etIp1)){
			return false;
		}
		
		if (!verificaPreenchido("O Ip deve ser preenchido corretamente.", etIp2)){
			return false;
		}
		
		if (!verificaPreenchido("O Ip deve ser preenchido corretamente.", etIp3)){
			return false;
		}
		
		if (!verificaPreenchido("O Ip deve ser preenchido corretamente.", etIp4)){
			return false;
		}
		
		if(!verificaPreenchido("O código do município deve ser preenchido.", etCodMun)){
			return false;
			
		} else if(etCodMun.getText().toString().length() != 7){
			toast("O código do município deve ter 7 dígitos.");
		}
		
		return verificaIps(etIp1, etIp2, etIp3, etIp4);
		
	}
	
	/**
	 * Verifica se cada campo de ip é maior que 255
	 * @return true se nenhum campo é maior que 255, false se algum campo é inválido
	 */
	public boolean verificaIps(EditText... args){
		
		for(int index = 0; index < args.length; index++){
			if(Integer.parseInt(args[index].getText().toString()) > 255){
				toast("Nenhum campo de Ip pode ser maior que 255.");
				return false;
			}
		}
		
		return true;
	}

}
