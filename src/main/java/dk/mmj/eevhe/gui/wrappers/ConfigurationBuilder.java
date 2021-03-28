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
    private Duration electionDuration = null;
    private Map<Integer, String> daAddresses;

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

        if (certFilePath == null || certKeyFilePath == null) {
            throw new BuildFailedException("Must specify both certificate and certificate secret-key");
        }

        final StringBuilder sb = new StringBuilder();

        if (outputFolder != null) {
            sb.append("--outputFolder=").append(outputFolder).append(" ");
        }

        sb.append("--cert=").append(certFilePath).append(" ");
        sb.append("--certKey=").append(certKeyFilePath).append(" ");
        sb.append("--time ").append(electionDuration).append(" ");
        sb.append("--addresses ");

        daAddresses.forEach((id, address) -> sb.append("-").append(id).append("_").append(address).append(" "));

        try {
            final SystemConfigurer.SystemConfiguration parse = parser.parse(sb.toString().split(" "));
            parse.produceInstance().run();
        } catch (NoSuchBuilderException | WrongFormatException e) {
            throw new BuildFailedException("Parse failed", e);
        }
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
