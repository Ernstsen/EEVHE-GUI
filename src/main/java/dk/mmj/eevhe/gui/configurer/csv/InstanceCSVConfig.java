package dk.mmj.eevhe.gui.configurer.csv;

import dk.mmj.eevhe.gui.configurer.ConfigurerManager;
import dk.mmj.eevhe.gui.configurer.InstanceType;

import java.util.Arrays;

/**
 * CSVConfig for decryption authority infos
 */
public class InstanceCSVConfig implements CSVConfig<ConfigurerManager.InstanceInfo> {
    @Override
    public boolean isValid(String[] line) {
        if (line.length != 3 || Arrays.stream(line).anyMatch(e -> e == null || e.isEmpty())) {
            return false;
        }

        try {
            final int i = Integer.parseInt(line[1]);
            return i > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public ConfigurerManager.InstanceInfo parse(String[] line) {
        return new ConfigurerManager.InstanceInfo(Integer.parseInt(line[1]), line[2], InstanceType.valueOf(line[0]));
    }
}
