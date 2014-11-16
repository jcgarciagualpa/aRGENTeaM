package ar.com.martinrevert.argenteam;

/**
 * Created by martin on 12/11/14.
 */
public class PostFacebook {
    private String titulo;
    private String fecha;
    private String imagen;
    private String linkpost;

    public PostFacebook(){

        titulo = "";
        fecha = "";
        imagen = "";
        linkpost = "";

    }

    public PostFacebook(String t, String f, String i, String l) {

        titulo = t;
        fecha = f;
        imagen = i;
        linkpost = l;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public String getLinkpost() {
        return linkpost;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setLinkpost(String linkpost) {
        this.linkpost = linkpost;
    }
}
