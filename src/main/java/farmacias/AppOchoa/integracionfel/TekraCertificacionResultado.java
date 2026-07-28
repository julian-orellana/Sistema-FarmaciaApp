package farmacias.AppOchoa.integracionfel;

public class TekraCertificacionResultado {

    private final boolean exitoso;
    private final int codigoError;
    private final String mensajeError;
    private final String uuid;
    private final String serie;
    private final String numero;
    private final String fechaCertificacion;
    private final String xmlCertificado;

    private TekraCertificacionResultado(
            boolean exitoso, int codigoError, String mensajeError, String uuid,
            String serie, String numero, String fechaCertificacion, String xmlCertificado){
        this.exitoso = exitoso;
        this.codigoError = codigoError;
        this.mensajeError = mensajeError;
        this.uuid = uuid;
        this.serie = serie;
        this.numero = numero;
        this.fechaCertificacion = fechaCertificacion;
        this.xmlCertificado = xmlCertificado;
    }

    public static TekraCertificacionResultado exito(String uuid, String serie, String numero,
                                                      String fechaCertificacion, String xmlCertificado){
        return new TekraCertificacionResultado(true, 0, null,  uuid, serie, numero, fechaCertificacion, xmlCertificado);
    }

    public static  TekraCertificacionResultado error(int codigoError, String mensajeError){
        return new TekraCertificacionResultado(false, codigoError, mensajeError,  null, null, null, null, null);
    }
    public static TekraCertificacionResultado exitoAnulacion(){
        return new TekraCertificacionResultado(true, 0, null, null, null, null, null, null);
    }

    public boolean isExitoso() {return exitoso;}
    public int getCodigoError() {return codigoError;}
    public String getMensajeError() {return mensajeError;}
    public String getUuid() {return uuid;}
    public String getSerie(){return serie;}
    public String getNumero() {return numero;}
    public String getFechaCertificacion(){return fechaCertificacion;}
    public String getXmlCertificado(){return xmlCertificado;}
}