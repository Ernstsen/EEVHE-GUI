package dk.mmj.eevhe.gui.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.eSoftware.commandLineParser.NoSuchBuilderException;
import dk.eSoftware.commandLineParser.SingletonCommandLineParser;
import dk.eSoftware.commandLineParser.WrongFormatException;
import dk.mmj.eevhe.entities.Candidate;
import dk.mmj.eevhe.initialization.SystemConfigurer;
import dk.mmj.eevhe.initialization.SystemConfigurerConfigBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigurationBuilder {
    private String outputFolder;
    private String certFilePath;
    private String certKeyFilePath;
    private Duration electionDuration = null;
    private Map<Integer, String> daAddresses;
    private Map<Integer, String> bbPeerAddresses;
    private List<Candidate> candidates;

    /**
     * @param outputFolder folder which output files are placed in
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * @param certFilePath Path to the election certificate
     */
    public void setCertFilePath(String certFilePath) {
        this.certFilePath = certFilePath;
    }


    /**
     * @param certKeyFilePath path to certificate secret-key
     */
    public void setCertKeyFilePath(String certKeyFilePath) {
        this.certKeyFilePath = certKeyFilePath;
    }

    /**
     * @param electionDuration how long the election will take in minutes
     */
    public void setElectionDuration(Duration electionDuration) {
        this.electionDuration = electionDuration;
    }

    /**
     * @param daAddresses map between ids and addresses for Decryption Authorities
     */
    public void setDaAddresses(Map<Integer, String> daAddresses) {
        this.daAddresses = daAddresses;
    }

    /**
     * @param bbPeerAddresses map between ids and addresses for bulletinBoard peers
     */
    public void setBbPeerAddresses(Map<Integer, String> bbPeerAddresses) {
        this.bbPeerAddresses = bbPeerAddresses;
    }

    /**
     * Executes the SystemConfigurer
     */
    public void build() throws BuildFailedException {
        final SingletonCommandLineParser<SystemConfigurer.SystemConfiguration> parser =
                new SingletonCommandLineParser<>(new SystemConfigurerConfigBuilder());

        if (daAddresses == null || daAddresses.isEmpty() || electionDuration == null) {
            throw new BuildFailedException("Failed to create configuration: " +
                    (electionDuration == null ? "duration undefined" :
                            "No Decryption Authority addresses were specified"));
        }
        if (bbPeerAddresses == null || bbPeerAddresses.isEmpty()) {
            throw new BuildFailedException("Failed to create configuration: No Bulletin Board addresses were specified");
        }

        if (certFilePath == null || certFilePath.trim().contains(" ") ||
                certKeyFilePath == null || certKeyFilePath.trim().contains(" ") ||
                outputFolder == null || outputFolder.trim().contains(" ")) {
            throw new BuildFailedException("Must specify both certificate, certificate secret-key and output-folder. No path is allowed to contain whitespace");
        }

        if (candidates == null || candidates.isEmpty()) {
            throw new BuildFailedException("Election must have candidates");
        }

        final StringBuilder sb = new StringBuilder();

        sb.append("--outputFolder=").append(outputFolder).append(" ");
        sb.append("--cert=").append(certFilePath).append(" ");
        sb.append("--certKey=").append(certKeyFilePath).append(" ");
        sb.append("--time ").append(electionDuration).append(" ");
        sb.append("--addresses ");

        daAddresses.forEach((id, address) -> sb.append("-").append(id).append("_").append(address).append(" "));
        sb.append("--bb_peer_addresses ");
        bbPeerAddresses.forEach((id, address) -> sb.append("-").append(id).append("_").append(address).append(" "));

        try {
            final SystemConfigurer.SystemConfiguration parse = parser.parse(sb.toString().split(" "));
            parse.produceInstance().run();

            final ObjectMapper mapper = new ObjectMapper();
            final File candidatesFile = new File(new File(outputFolder), "candidates.json");
            mapper.writeValue(candidatesFile, candidates);
        } catch (NoSuchBuilderException | WrongFormatException e) {
            throw new BuildFailedException("Parse failed", e);
        } catch (IOException e) {
            throw new BuildFailedException("Failed to write candidates file", e);
        }
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public static class Duration {
        int days;
        int hours;
        int minutes;

        public Duration(int days, int hours, int minutes) {
            this.days = days;
            this.hours = hours;
            this.minutes = minutes;
        }

        @Override
        public String toString() {
            return "-day=" + days + " -hour=" + hours + " -min=" + minutes;
        }
    }
}
