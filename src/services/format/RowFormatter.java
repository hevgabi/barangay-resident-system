package services.format;

public interface RowFormatter<T> {
    String[] headers();
    String[] toRow(T item);
    
}
