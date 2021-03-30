package dk.mmj.eevhe.gui.wrappers;

import dk.mmj.eevhe.client.Voter;
import dk.mmj.eevhe.entities.Candidate;

import java.util.List;

public class VoterWrapper {
    private final Voter voter;

    /**
     * Initializes a voter
     *
     * @param bulletinBoard BB path
     * @param id            voterId
     */
    public VoterWrapper(String bulletinBoard, String id) {
        voter = new Voter(bulletinBoard, id);
    }

    /**
     * Casts a vote
     *
     * @param vote idx of candidate to vote for
     * @throws VoteFailedException if one or more requirements is not met
     */
    public void vote(int vote) throws VoteFailedException {
        if (!voter.checkBulletinBoard()) {
            throw new VoteFailedException("Target URL was not bulletinBoard");
        }
    }

    /**
     * Fetches list of candidates in election
     *
     * @return list of candidates
     */
    public List<Candidate> getCandidates() {
        return voter.getCandidates();
    }

}
