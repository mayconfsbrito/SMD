/**
 * 
 */
package ufop.smd.persistencia;

/**
 * @author MAYCON
 *
 */
public interface InterfaceScriptSQLite {

	public static final String NOME = "smd_android";
	public static final Integer VERSAO = 10;

	public static final String SCRIPT_SQLITE_CREATE_AGENTE = "CREATE TABLE `agente` (`idAgente` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , `codigo` INT  NOT NULL ,`nome` VARCHAR(100)  NOT NULL, `ativo` SMALLINT  NOT NULL, `login` VARCHAR(45)  NOT NULL, `senha` VARCHAR(64)  NOT NULL, `dataNascimento` DATE  NULL DEFAULT NULL,`telefone` VARCHAR(45)  NULL DEFAULT NULL, `dataCadastro` DATE  NOT NULL , `horaCadastro` TIME  NOT NULL, `numeroAmostra` INTEGER NOT NULL, `anoAmostra` INTEGER NOT NULL);";
	
	public static final String SCRIPT_SQLITE_CREATE_BOLETIMPESQUISA = 
			"CREATE TABLE `boletimpesquisa` (`idBoletimPesquisa` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`coordenadorRegional` VARCHAR(45)  NOT NULL , `grs` VARCHAR(2)  NOT NULL , `idMunicipio` INT  NOT NULL , `idLocalidade` INT  NOT NULL , `categoria` VARCHAR(6)  NOT NULL , `idAgente` INT  NOT NULL , `turma` VARCHAR(4)  NOT NULL , `data` DATE  NOT NULL , `semanaEpid` VARCHAR(7)  NOT NULL , `numeroAtividade` VARCHAR(7)  NOT NULL , `liAmostra` INT, `tipoAtividade` VARCHAR(7)  NOT NULL , `sincronizadoEm` DATE  NULL DEFAULT NULL, `ativo` SMALLINT  NOT NULL, CONSTRAINT `fk_BoletimPesquisa_Localidade10` FOREIGN KEY (`idLocalidade`) REFERENCES localidade (`idLocalidade`), CONSTRAINT `fk_BoletimPesquisa_agente10` FOREIGN KEY (`idAgente`) REFERENCES agente (`idAgente`), CONSTRAINT `fk_BoletimPesquisa_Municipio10` FOREIGN KEY (`idMunicipio`) REFERENCES municipio (`idMunicipio`));";
	
	public static final String SCRIPT_SQLITE_CREATE_BOLETIMTRATAMENTO =
			"CREATE TABLE `boletimtratamento` (`idBoletimTratamento` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `coordenadorRegional` VARCHAR(45)  NOT NULL , `grs` VARCHAR(2)  NOT NULL , `idMunicipio` INT  NOT NULL , `idLocalidade` INT  NOT NULL , `categoria` VARCHAR(6)  NOT NULL , `idAgente` INT  NOT NULL , `turma` VARCHAR(4)  NOT NULL , `data` DATE  NOT NULL , `semanaEpid` VARCHAR(7)  NOT NULL , `numeroAtividade` VARCHAR(7)  NOT NULL , `tipoAtividade` VARCHAR(7)  NOT NULL , `sincronizadoEm` DATE  NULL DEFAULT NULL, `ativo` SMALLINT  NOT NULL , CONSTRAINT `fk_BoletimTratamento_Localidade10` FOREIGN KEY (`idLocalidade`) REFERENCES localidade (`idLocalidade`), CONSTRAINT `fk_BoletimTratamento_agente1` FOREIGN KEY (`idAgente`) REFERENCES agente (`idAgente`), CONSTRAINT `fk_BoletimTratamento_Municipio10` FOREIGN KEY (`idMunicipio`) REFERENCES municipio (`idMunicipio`));"; 
	
	public static final String SCRIPT_SQLITE_CREATE_IMOVEL =
			"CREATE TABLE `imovel` (`idImovel` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`idQuadra` INT  NOT NULL , `idLogradouro` INT  NOT NULL , `numero` VARCHAR(15)  NOT NULL , `complemento` VARCHAR(20)  NULL DEFAULT NULL, CONSTRAINT `fk_Imovel_Quadra1` FOREIGN KEY (`idQuadra`) REFERENCES quadra (`idQuadra`), CONSTRAINT `fk_Imovel_Logradouro1` FOREIGN KEY (`idLogradouro`) REFERENCES logradouro (`idLogradouro`));"; 
	
	public static final String SCRIPT_SQLITE_CREATE_LOCALIDADE = 
			"CREATE TABLE `localidade` (`idLocalidade` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`idMunicipio` INT  NOT NULL , `codigo` INT  NOT NULL , `nome` VARCHAR(100)  NOT NULL , `croqui` SMALLINT  NOT NULL , `dataCroqui` DATE  NULL DEFAULT NULL, `horaCroqui` TIME  NULL DEFAULT NULL,CONSTRAINT `fk_Localidade_Municipio1` FOREIGN KEY (`idMunicipio`) REFERENCES municipio (`idMunicipio`));"; 	
	
	public static final String SCRIPT_SQLITE_CREATE_LOGRADOURO = 
			"CREATE TABLE `logradouro` (`idLogradouro` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`idMunicipio` INT  NOT NULL , `nome` VARCHAR(100)  NOT NULL , CONSTRAINT `fk_Logradouro_Municipio1` FOREIGN KEY (`idMunicipio`) REFERENCES municipio (`idMunicipio`));"; 
	
	public static final String SCRIPT_SQLITE_CREATE_MUNICIPIO =
			"CREATE TABLE `municipio` (`idMunicipio` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`codigo` VARCHAR(7)  NOT NULL , `nome` VARCHAR(100)  NOT NULL , `estado` VARCHAR(2)  NOT NULL );";
	
	public static final String SCRIPT_SQLITE_CREATE_QUADRA =
			"CREATE TABLE `quadra` (`idQuadra` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`idLocalidade` INT  NOT NULL , `codigo` INT  NOT NULL , CONSTRAINT `fk_Quadra_Localidade1` FOREIGN KEY (`idLocalidade`) REFERENCES localidade (`idLocalidade`));";
	
	public static final String SCRIPT_SQLITE_CREATE_QUADRALOGRADOURO =
			"CREATE TABLE `quadralogradouro` (`idQuadraLogradouro` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,`idLogradouro` INT  NOT NULL , `idQuadra` INT  NOT NULL , CONSTRAINT `fk_Imovel_has_Logradouro_Logradouro1` FOREIGN KEY (`idLogradouro`) REFERENCES logradouro (`idLogradouro`), CONSTRAINT `fk_ImovelLogradouro_Quadra1` FOREIGN KEY (`idQuadra`) REFERENCES quadra (`idQuadra`));";
	
	public static final String SCRIPT_SQLITE_CREATE_VISITAPESQUISA =
			"CREATE TABLE `visitapesquisa` (`idVisitaPesquisa` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,`idBoletimPesquisa` INT  NOT NULL , `idQuadra` INT  NOT NULL , `idLogradouro` INT  NOT NULL , `idImovel` INT  NOT NULL , `tipoUnidade` VARCHAR(45)  NOT NULL , `numeroTubitos` INT  NULL DEFAULT NULL, `numeroAmostra` INT  NULL DEFAULT NULL, `larvasAeg` INT  NULL DEFAULT NULL, `larvasAlb` INT  NULL DEFAULT NULL, `larvasOut` INT  NULL DEFAULT NULL, `examinadoLaboratorio` SMALLINT  NOT NULL ,`depositosInspecionados` INT  NOT NULL , `ultimaVisitaBoletim` SMALLINT  NOT NULL , `quadraConcluida` SMALLINT NOT NULL, `hora` TIME  NOT NULL , CONSTRAINT `fk_VisitaPesquisa_Endereco0` FOREIGN KEY (`idLogradouro`) REFERENCES logradouro (`idLogradouro`), CONSTRAINT `fk_VisitaPesquisa_Quadra1` FOREIGN KEY (`idQuadra`) REFERENCES quadra (`idQuadra`), CONSTRAINT `fk_VisitaPesquisa_Imovel1` FOREIGN KEY (`idImovel`) REFERENCES imovel (`idImovel`), CONSTRAINT `fk_VisitaPesquisa_BoletimPesquisa1` FOREIGN KEY (`idBoletimPesquisa`) REFERENCES boletimpesquisa (`idBoletimPesquisa`));";
	
	public static final String SCRIPT_SQLITE_CREATE_VISITATRATAMENTO = 
			"CREATE TABLE `visitatratamento` (`idVisitaTratamento` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,`idBoletimTratamento` INT  NOT NULL , `idQuadra` INT  NOT NULL , `idLogradouro` INT  NOT NULL , `idImovel` INT  NOT NULL , `tipoUnidade` VARCHAR(45)  NOT NULL , `inseticidaLarvicida` INT  NULL DEFAULT NULL,`inseticidaAdulticida` INT  NULL DEFAULT NULL, `pendenciaRec` SMALLINT  NULL DEFAULT NULL, `pendenciaFech` SMALLINT  NULL DEFAULT NULL, `pendenciaResg` SMALLINT  NULL DEFAULT NULL, `depositosTratados` INT  NOT NULL , `ultimaVisitaBoletim` SMALLINT  NOT NULL , `quadraConcluida` SMALLINT NOT NULL, `hora` TIME  NOT NULL , CONSTRAINT `fk_VisitaTratamento_Endereco` FOREIGN KEY (`idLogradouro`) REFERENCES logradouro (`idLogradouro`), CONSTRAINT `fk_VisitaTratamento_Quadra1` FOREIGN KEY (`idQuadra`) REFERENCES quadra (`idQuadra`), CONSTRAINT `fk_VisitaTratamento_Imovel1` FOREIGN KEY (`idImovel`) REFERENCES imovel (`idImovel`), CONSTRAINT `fk_VisitaTratamento_BoletimTratamento1` FOREIGN KEY (`idBoletimTratamento`) REFERENCES boletimtratamento (`idBoletimTratamento`));";
	
	public static final String SCRIPT_SQLITE_CREATE_CONFIGURACAO = 
			"CREATE TABLE `configuracao` (`id` INTEGER PRIMARY KEY NOT NULL, `porta` INTEGER NOT NULL , `ipServidor` VARCHAR(15) NOT NULL, `codigoMunicipio` VARCHAR(7) NOT NULL);";
	
	public static final String[] SCRIPT_SQLITE_CREATE_INDEX = {
		"CREATE INDEX 'fk_BoletimPesquisa_Localidade1' ON 'boletimpesquisa' (`idLocalidade` DESC);",
		"CREATE INDEX 'fk_BoletimPesquisa_Municipio1' ON 'boletimpesquisa' (`idMunicipio` DESC);",
		"CREATE INDEX 'fk_BoletimPesquisa_agente1' ON 'boletimpesquisa' (`idAgente` DESC);",
		"CREATE INDEX 'fk_BoletimTratamento_Localidade1' ON 'boletimtratamento' (`idLocalidade` DESC);",
		"CREATE INDEX 'fk_BoletimTratamento_Municipio1' ON 'boletimtratamento' (`idMunicipio` DESC);",
		"CREATE INDEX 'fk_BoletimTratamento_agente1' ON 'boletimtratamento' (`idAgente` DESC);",
		"CREATE INDEX 'fk_ImovelLogradouro_Quadra1' ON 'quadralogradouro' (`idQuadra` DESC);",
		"CREATE INDEX 'fk_Imovel_Logradouro1' ON 'imovel' (`idLogradouro` DESC);",
		"CREATE INDEX 'fk_Imovel_Quadra1' ON 'imovel' (`idQuadra` DESC);",
		"CREATE INDEX 'fk_Imovel_has_Logradouro_Logradouro1' ON 'quadralogradouro' (`idLogradouro` DESC);",
		"CREATE INDEX 'fk_Localidade_Municipio1' ON 'localidade' (`idMunicipio` DESC);",
		"CREATE INDEX 'fk_Logradouro_Municipio1' ON 'logradouro' (`idMunicipio` DESC);",
		"CREATE INDEX 'fk_Quadra_Localidade1' ON 'quadra' (`idLocalidade` DESC);",
		"CREATE INDEX 'fk_VisitaPesquisa_BoletimPesquisa1' ON 'visitapesquisa' (`idBoletimPesquisa` DESC);",
		"CREATE INDEX 'fk_VisitaPesquisa_Endereco' ON 'visitapesquisa' (`idLogradouro` DESC);",
		"CREATE INDEX 'fk_VisitaPesquisa_Imovel1' ON 'visitapesquisa' (`idImovel` DESC);",
		"CREATE INDEX 'fk_VisitaPesquisa_Quadra1' ON 'visitapesquisa' (`idQuadra` DESC);",
		"CREATE INDEX 'fk_VisitaTratamento_BoletimTratamento1' ON 'visitatratamento' (`idBoletimTratamento` DESC);",
		"CREATE INDEX 'fk_VisitaTratamento_Endereco' ON 'visitatratamento' (`idLogradouro` DESC);",
		"CREATE INDEX 'fk_VisitaTratamento_Imovel1' ON 'visitatratamento' (`idImovel` DESC);",
		"CREATE INDEX 'fk_VisitaTratamento_Quadra1' ON 'visitatratamento' (`idQuadra` DESC);"
		};
	
	public static final String SCRIPT_SQLITE_DROP_AGENTE = 
			"DROP TABLE IF EXISTS agente;";
	public static final String SCRIPT_SQLITE_DROP_BOLETIMPESQUISA =
			"DROP TABLE IF EXISTS boletimpesquisa;";
	public static final String SCRIPT_SQLITE_DROP_BOLETIMTRATAMENTO =
			"DROP TABLE IF EXISTS boletimtratamento;";
	public static final String SCRIPT_SQLITE_DROP_IMOVEL = 
			"DROP TABLE IF EXISTS imovel;";
	public static final String SCRIPT_SQLITE_DROP_LOCALIDADE =
			"DROP TABLE IF EXISTS localidade;";
	public static final String SCRIPT_SQLITE_DROP_LOGRADOURO =
			"DROP TABLE IF EXISTS logradouro; ";
	public static final String SCRIPT_SQLITE_DROP_MUNICIPIO =
			"DROP TABLE IF EXISTS municipio; ";
	public static final String SCRIPT_SQLITE_DROP_QUADRA =
			"DROP TABLE IF EXISTS quadra; ";
	public static final String SCRIPT_SQLITE_DROP_QUADRALOGRADOURO =
			"DROP TABLE IF EXISTS quadralogradouro; ";
	public static final String SCRIPT_SQLITE_DROP_VISITAPESQUISA =
			"DROP TABLE IF EXISTS visitapesquisa; ";
	public static final String SCRIPT_SQLITE_DROP_VISITATRATAMENTO =
			"DROP TABLE IF EXISTS visitatratamento; ";
	public static final String SCRIPT_SQLITE_DROP_CONFIGURACAO =
			"DROP TABLE IF EXISTS configuracao; ";
}