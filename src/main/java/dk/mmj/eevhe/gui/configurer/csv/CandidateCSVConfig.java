package dk.mmj.eevhe.gui.configurer.csv;

import dk.mmj.eevhe.entities.Candidate;

import java.util.Arrays;

/**
 * CSVConfig for Candidates
 */
public class CandidateCSVConfig implements CSVConfig<Candidate> {
    @Override
    public boolean isValid(String[] line) {
        if (line.length != 3 || Arrays.stream(line).anyMatch(e -> e == null || e.isEmpty())) {
            return false;
        }

        try {
            final int i = Integer.parseInt(line[0]);
            return i >= 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public Candidate parse(String[] line) {
        return new Candidate(Integer.parseInt(line[0]), line[1], line[2]);
    }
}
