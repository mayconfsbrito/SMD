package ufop.smd.controle;

import android.os.Handler;

public interface Constantes {

	/**
	 * Diretorios da aplicacao
	 */
	public static final String DIRETORIO_CROQUIS = "/ufop.smd/croquis/";
	
	/**
	 * Constantes do sistema
	 */
	public static final int TOAST_DURATION = 100000;

	/**
	 * Constantes dos menus
	 */
	public static final int MENU_LOGOFF = 1;
	public static final int MENU_CADASTRAR = 2;
	public static final int MENU_VISITAS = 3;
	public static final int MENU_SINCRONIZAR_USUARIOS = 4;
	public static final int MENU_CONFIGURACAO = 5;

	/**
	 * Constantes das telas do sistema
	 */
	public static int TELA_LOGIN = 1;
	public static int TELA_OPCOES = 2;
	public static int TELA_LOCALIDADE_CROQUI = 3;
	public static int TELA_CROQUI = 4;
	public static int TELA_BOLETIM_TRATAMENTO_LISTAGEM = 5;
	public static int TELA_BOLETIM_TRATAMENTO_VISUALIZAR = 6;
	public static int TELA_BOLETIM_PESQUISA_LISTAGEM = 7;
	public static int TELA_BOLETIM_PESQUISA_VISUALIZAR = 8;
	public static int TELA_VISITA_TRATAMENTO_LISTAGEM = 9;
	public static int TELA_VISITA_TRATAMENTO_VISUALIZAR = 10;
	public static int TELA_VISITA_PESQUISA_LISTAGEM = 11;
	public static int TELA_VISITA_PESQUISA_VISUALIZAR = 12;
	public static int TELA_BUSCA = 13;
	public static int TELA_SINCRONIZACAO = 14;
	public static int TELA_CONFIGURACAO = 15;	

	/**
	 * Constantes de retorno de activitys
	 */
	public static int LOGOFF = 100;
	public static int ERRO_IMAGEM = 101;
	public static int QUADRA = 102;
	public static int LOGRADOURO = 103;
	public static int IMOVEL = 104;

	/**
	 * Constantes que representam os pacotes de inseticidas utilizados no boletim de tratamento
	 */
	public static final int NOVO_PACOTE = 2;
	public static final int PACOTE_USADO = 1;
	public static final int NAO_USADO = 0;

	/**
	 * Constantes que representam as mensagens de sincronizacao entre servidor e android
	 */
	public static final int BUFFER_SIZE = 1024;
	public static final String SINC_USUARIOS = "SINC_USUARIOS";
	public static final String SINC_COMPLETO = "SINC_COMPLETO";
	public static final String SINC_CROQUI = "SINC_CROQUI";
	public static final String SINC_BOL = "SINC_BOL";
	public static final String OK = "OK";
    public static final String CANCEL = "CANCEL";
    public static final String CONCLUIDO = "CONCLUIDO";
    /**
     * Requisitos do Sistema
     */
    public static final int MAXIMO_VISITAS_POR_BOLETIM = 25;

}
