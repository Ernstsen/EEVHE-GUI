package dk.mmj.eevhe.gui.wrappers;

import dk.mmj.eevhe.client.ResultFetcher;
import dk.mmj.eevhe.client.results.ElectionResult;
import dk.mmj.eevhe.entities.Candidate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ResultFetcherWrapper {

    private final ResultFetcher resultFetcher;

    public ResultFetcherWrapper(String address) {
        resultFetcher = new ResultFetcher(address, false);
    }

    public CompleteElectionResult getResults() {
        resultFetcher.run();
        ElectionResult electionResult = null;
        try {
            electionResult = resultFetcher.getElectionResult();
        } catch (Exception ignored) {
        }

        if (electionResult == null) {
            return null;
        }
        final List<Integer> candidateVotes = electionResult.getCandidateVotes();
        final List<Candidate> candidates = resultFetcher.getCandidates();
        final float votesTotal = electionResult.getVotesTotal();

        final List<CandidateWithResult> candidateResults = IntStream.range(0, candidates.size())
                .mapToObj(idx -> new CandidateWithResult(
                        candidates.get(idx),
                        candidateVotes.get(idx),
                        (((float) candidateVotes.get(idx)) / votesTotal) * 100f
                ))
                .collect(Collectors.toList());

        return new CompleteElectionResult(candidateResults);
    }

    public static class CompleteElectionResult {
        private final List<CandidateWithResult> candidateResults;

        public CompleteElectionResult(List<CandidateWithResult> candidateResults) {
            this.candidateResults = candidateResults;
        }

        public List<CandidateWithResult> getCandidateResults() {
            return candidateResults;
        }

    }

    @SuppressWarnings("unused")
    public static class CandidateWithResult {

        public final int idx;
        public final String name;
        public final String description;
        public final int votes;
        public final float votesPercentage;

        public CandidateWithResult(Candidate candidate, int votes, float votesPercentage) {
            this.idx = candidate.getIdx();
            this.name = candidate.getName();
            this.description = candidate.getDescription();
            this.votes = votes;
            this.votesPercentage = votesPercentage;
        }

        public int getIdx() {
            return idx;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getVotes() {
            return votes;
        }

        public float getVotesPercentage() {
            return votesPercentage;
        }
    }


}
