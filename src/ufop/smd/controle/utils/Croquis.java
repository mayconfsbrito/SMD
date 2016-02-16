package ufop.smd.controle.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Croquis {
	
	Context context = null;
	
	//Diretório de croquis
	public String diretorioCroquis = null; 
	
	public Croquis(Context context){
		this.context = context;
		this.diretorioCroquis = Environment.getExternalStorageDirectory() + "/croquis";
	}
	
	/**
	 * Copia uma imagem de outro local para a pasta @croquis
	 * @throws IOException 
	 * @path endereco da imagem
	 */
	public boolean copiaImagem(String path, String nome) throws IOException{
		
		FileChannel sourceChannel = null;
        FileChannel destinationChannel = null;

        try {
        	Log.d("smd", "Copiando a imagem de " + path + " para a pasta de croquis...");
            sourceChannel = new FileInputStream(path).getChannel();
            destinationChannel = new FileOutputStream(diretorioCroquis +"/" + nome).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
            
            destinationChannel.close();
            sourceChannel.close();
            
            Log.d("smd", "Imagem copiada com sucesso!");

        } catch(Exception er){
        	Excecao.notificacao("Não foi possível copiar a imagem " + path, er);
        }finally {
            if (sourceChannel != null && sourceChannel.isOpen()) {
                sourceChannel.close();
            }
            if (destinationChannel != null && destinationChannel.isOpen()) {
                destinationChannel.close();
            }
        }
		
		return true;
	}
	
	/**
	 * Busca a imagem de um croqui na pasta @croquis
	 */
	public boolean getImagem(int id){
		
		return true;
	}
	
	/**
	 * Insere a imagem de um croqui na pasta @croquis
	 */
	public boolean setImagem(int id){
		
		Log.d("smd", "Inserindo imagem...");
		
		try {
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		Log.d("smd", "Imagem inserida com sucesso!");
		
		return true;
	}
	
	/**
	 * Cria o novo diretorio @croquis
	 * @return
	 */
	public boolean criarDiretorio(){
		
		try {

			File file = new File(Environment.getExternalStorageDirectory() + "/croquis");
		
			if(!file.exists()){
				
				Log.d("smd", "Criando o diretório de croquis...");
				Log.d("smd", Environment.getExternalStorageDirectory() + "/croquis");
				
				file.mkdir();

				Log.d("smd", "Diretório croquis criado em " + diretorioCroquis );
			
			}
			
		} catch (Exception e) {
			Log.d("smd", "Não foi possível criar o diretório croquis em " + Environment.getExternalStorageDirectory() + "/croquis");
			e.printStackTrace();
		}
		
		
		return true;
	}
}
