package org.pine.blockparty.model;

public record PlayerStats(int wins, int losses, int ties, int roundsSurvived) {

    @Override
    public String toString() {
        final int totalGames = totalGamesPlayed();
        final int winPercentage = (totalGames > 0) ? (wins * 100) / totalGames : 0;
        return String.format("---Your stats---\nTotal games played: %d\nWins: %d\nLosses: %d\nTies: %d\nWin rate: %d%%\nRounds survived: %d",
                totalGames, wins, losses, ties, winPercentage, roundsSurvived);
    }

    public int totalGamesPlayed() {
        return wins + losses + ties;
    }
}
