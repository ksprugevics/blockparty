package org.pine.blockparty.model;

public class PlayerStats {

    private int wins;
    private int losses;
    private int ties;
    private int roundsSurvived;

    public PlayerStats(int wins, int losses, int ties, int roundsSurvived) {
        this.wins = wins;
        this.losses = losses;
        this.ties = ties;
        this.roundsSurvived = roundsSurvived;
    }

    public int getTotalGamesPlayed() {
        return wins + losses + ties;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }

    public int getRoundsSurvived() {
        return roundsSurvived;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLosses() {
        losses++;
    }

    public void incrementTies() {
        ties++;
    }

    public void incrementRoundsSurvived() {
        roundsSurvived++;
    }

    @Override
    public String toString() {
        final int totalGames = getTotalGamesPlayed();
        final int winPercentage = (totalGames > 0) ? (wins * 100) / totalGames : 0;
        return String.format("---Your stats---\nTotal games played: %d\nWins: %d\nLosses: %d\nTies: %d\nWin rate: %d%%\nRounds survived: %d",
                totalGames, wins, losses, ties, winPercentage, roundsSurvived);
    }
}
