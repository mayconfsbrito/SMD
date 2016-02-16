package ufop.smd.modelo;
// Generated 21/03/2012 13:54:52 by Hibernate Tools 3.2.1.GA


import android.content.ContentValues;
import android.database.Cursor;

/**
 * Logradouro generated by hbm2java
 */
public class Logradouro implements java.io.Serializable {


     private Integer idLogradouro;
     private Integer idMunicipio;
     private String nome;

    public Logradouro() {
    }

	
    public Logradouro(Integer municipio, String nome) {
        this.idMunicipio = municipio;
        this.nome = nome;
    }
    public Logradouro(Cursor cursor){
    	setIdLogradouro(cursor.getInt(0));
    	setIdMunicipio(cursor.getInt(1));
    	setNome(cursor.getString(2));
    }
   
	public ContentValues getContentValues(){

		ContentValues values = new ContentValues();

		values.put("idLogradouro", this.getIdLogradouro() != null ? this.getIdLogradouro() : null);
		values.put("idMunicipio", this.getIdMunicipio());
		values.put("nome", this.getNome());

		return values;
	}
    
    public Integer getIdLogradouro() {
        return this.idLogradouro;
    }
    
    public void setIdLogradouro(Integer idLogradouro) {
        this.idLogradouro = idLogradouro;
    }
    public Integer getIdMunicipio() {
        return this.idMunicipio;
    }
    
    public void setIdMunicipio(Integer municipio) {
        this.idMunicipio = municipio;
    }
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object obj){
        
        Logradouro outro = (Logradouro) obj;
        if(this.getIdLogradouro().equals(outro.getIdLogradouro())){
            return true;
        }
        
        if(this.getIdMunicipio().equals(outro.getIdMunicipio())
                && this.getNome().equals(outro.getNome())){
            return true;
            
        } else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.idLogradouro != null ? this.idLogradouro : 0);
        hash = 47 * hash + (this.idMunicipio != null ? this.idMunicipio : 0);
        hash = 47 * hash + (this.nome != null ? this.nome.hashCode() : 0);
        return hash;
    }


}

