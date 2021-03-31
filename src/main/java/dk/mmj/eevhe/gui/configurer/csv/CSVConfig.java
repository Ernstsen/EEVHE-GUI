package dk.mmj.eevhe.gui.configurer.csv;

public interface CSVConfig<T> {

    /**
     * Determines whether a given line is valid
     *
     * @param line the line
     * @return whether the line is valid
     */
    boolean isValid(String[] line);

    /**
     * Parses an object
     *
     * @param line valid line
     * @return object parsed from line
     */
    T parse(String[] line);
}
