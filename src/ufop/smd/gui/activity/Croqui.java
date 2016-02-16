package ufop.smd.gui.activity;

import ufop.smd.R;
import ufop.smd.controle.Constantes;
import ufop.smd.controle.Login;
import ufop.smd.controle.utils.Excecao;
import ufop.smd.gui.imageview.ImageViewTouch;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Croqui extends ActivityPai {

	private ImageViewTouch	mImageView;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView(R.layout.croqui);
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );

		inicializaComponentes();
	}

	@Override
	public void onContentChanged()
	{
		super.onContentChanged();
		mImageView = (ImageViewTouch)findViewById( R.id.imgView );
	}

	@Override
	public boolean inicializaComponentes() {

		getImagem();

		return true;
	}

	/**
	 * Troca a imagem exibida pela activity
	 */
	public void getImagem(){

		toast("Carregando croqui.");

		try {

			Cursor c = getContentResolver().query( Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null );

			if ( c != null ) {

				int count = c.getCount();
				int position = (int)( Math.random() * count );

				if ( c.moveToPosition( position ) ) {

					int orientation = c.getInt( c.getColumnIndex( Images.Media.ORIENTATION ) );

					Bitmap bitmap;
					
					Log.d("ufop.smd", "Carregando Croqui em: " + Environment.getExternalStorageDirectory() + Constantes.DIRETORIO_CROQUIS 
							+ getIntent().getExtras().getInt("idLocalidade") + ".png");
					
					bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + Constantes.DIRETORIO_CROQUIS 
							+ getIntent().getExtras().getInt("idLocalidade") + ".png");
					mImageView.setImageBitmapReset( bitmap, orientation, true );
				}
			}

			c.close();
			c = null;

		} catch (Exception e ) {

			setResult(Constantes.ERRO_IMAGEM);
			finish();
		}

	}

	@Override
	public boolean addListeners() {
		return false;
	}

}
