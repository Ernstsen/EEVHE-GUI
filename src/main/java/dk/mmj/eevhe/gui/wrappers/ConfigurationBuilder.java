package dk.mmj.eevhe.gui.wrappers;

import dk.eSoftware.commandLineParser.NoSuchBuilderException;
import dk.eSoftware.commandLineParser.SingletonCommandLineParser;
import dk.eSoftware.commandLineParser.WrongFormatException;
import dk.mmj.eevhe.initialization.SystemConfigurer;
import dk.mmj.eevhe.initialization.SystemConfigurerConfigBuilder;

import java.util.Map;

public class ConfigurationBuilder {
    private String outputFolder;
    private String certFilePath;
    private String certKeyFilePath;
    private long electionDuration = -1;
    private Map<Integer, String> daAddresses;

    /**
     * @return current outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }

    /**
     * @param outputFolder folder which output files are placed in
     */
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * @return file containing certificate
     */
    public String getCertFilePath() {
        return certFilePath;
    }

    /**
     * @param certFilePath Path to the election certificate
     */
    public void setCertFilePath(String certFilePath) {
        this.certFilePath = certFilePath;
    }

    /**
     * @return file containing secret-key for the certificate
     */
    public String getCertKeyFilePath() {
        return certKeyFilePath;
    }

    /**
     * @param certKeyFilePath path to certificate secret-key
     */
    public void setCertKeyFilePath(String certKeyFilePath) {
        this.certKeyFilePath = certKeyFilePath;
    }

    /**
     * @return duration of the election, in minutes
     */
    public long getElectionDuration() {
        return electionDuration;
    }

    /**
     * @param electionDuration how long the election will take in minutes
     */
    public void setElectionDuration(long electionDuration) {
        this.electionDuration = electionDuration;
    }

    /**
     * @return Map of registered DA addresses
     */
    public Map<Integer, String> getDaAddresses() {
        return daAddresses;
    }

    /**
     * @param daAddresses map between ids and addresses for Decryption Authorities
     */
    public void setDaAddresses(Map<Integer, String> daAddresses) {
        this.daAddresses = daAddresses;
    }

    /**
     * Executes the SystemConfigurer
     */
    public void build() throws BuildFailedException {
        final SingletonCommandLineParser<SystemConfigurer.SystemConfiguration> parser =
                new SingletonCommandLineParser<>(new SystemConfigurerConfigBuilder());

        if (daAddresses == null || daAddresses.isEmpty() || electionDuration < 1) {
            throw new BuildFailedException("Failed to create configuration: " +
                    (electionDuration < 1 ? "duration undefined" :
                            "No Decryption Authority addresses were specified"));
        }

        if (certFilePath == null || certKeyFilePath == null) {
            throw new BuildFailedException("Must specify both certificate and certificate secret-key");
        }

        final StringBuilder sb = new StringBuilder();

        if (outputFolder != null) {
            sb.append("--outputFolder=").append(outputFolder).append(" ");
        }

        sb.append("--cert=").append(certFilePath).append(" ");
        sb.append("--certKey=").append(certKeyFilePath).append(" ");
        sb.append("--time -min=").append(electionDuration).append(" ");
        sb.append("--addresses ");

        daAddresses.forEach((id, address) -> sb.append("-").append(id).append("_").append(address).append(" "));

        try {
            final SystemConfigurer.SystemConfiguration parse = parser.parse(sb.toString().split(" "));
            parse.produceInstance().run();
        } catch (NoSuchBuilderException | WrongFormatException e) {
            throw new BuildFailedException("Parse failed", e);
        }
    }
}
