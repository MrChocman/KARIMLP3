import java.io.Serializable;

public class ParAsociado<T, U> implements Serializable {

    private static final long serialVersionUID = 1L;
    private T primero;
    private U segundo;

    public ParAsociado(T primero, U segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }

    public T getPrimero() { return primero; }
    public U getSegundo() { return segundo; }

    @Override
    public String toString() {
        return primero.toString() + " \n  --> " + segundo.toString();
    }
}