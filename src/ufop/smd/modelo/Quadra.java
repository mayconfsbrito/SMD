package ufop.smd.modelo;
// Generated 21/03/2012 13:54:52 by Hibernate Tools 3.2.1.GA


import android.content.ContentValues;
import android.database.Cursor;

/**
 * Quadra generated by hbm2java
 */
public class Quadra  implements java.io.Serializable {


	private Integer idQuadra;
	private Integer idLocalidade;
	private Integer codigo;

	public Quadra() {
	}
	public Quadra(Integer localidade, Integer codigo) {
		this.idLocalidade = localidade;
		this.codigo = codigo;
	}
	public Quadra(Cursor cursor){
		setIdQuadra(cursor.getInt(0));
		setIdLocalidade(cursor.getInt(1));
		setCodigo(cursor.getInt(2));
	}

	public ContentValues getContentValues(){

		ContentValues values = new ContentValues();

		values.put("idQuadra", this.getIdQuadra() != null ? this.getIdQuadra() : null);
		values.put("idLocalidade", this.getIdLocalidade());
		values.put("codigo", this.getCodigo());
		
		return values;
	}

	public Integer getIdQuadra() {
		return this.idQuadra;
	}

	public void setIdQuadra(Integer idQuadra) {
		this.idQuadra = idQuadra;
	}
	public Integer getIdLocalidade() {
		return this.idLocalidade;
	}

	public void setIdLocalidade(Integer localidade) {
		this.idLocalidade = localidade;
	}
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	

}


