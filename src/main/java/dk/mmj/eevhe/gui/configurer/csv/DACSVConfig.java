package dk.mmj.eevhe.gui.configurer.csv;

import dk.mmj.eevhe.gui.configurer.ConfigurerManager;

import java.util.Arrays;

/**
 * CSVConfig for decryption authority infos
 */
public class DACSVConfig implements CSVConfig<ConfigurerManager.DAInfo> {
    @Override
    public boolean isValid(String[] line) {
        if (line.length != 2 || Arrays.stream(line).anyMatch(e -> e == null || e.isEmpty())) {
            return false;
        }

        try {
            final int i = Integer.parseInt(line[0]);
            return i > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public ConfigurerManager.DAInfo parse(String[] line) {
        return new ConfigurerManager.DAInfo(Integer.parseInt(line[0]), line[1]);
    }
}
