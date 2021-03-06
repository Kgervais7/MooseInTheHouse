package models.game;

import models.card.Card;
import models.player.Bot;
import models.player.Player;
import view.CardObserver;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static Player[] players;
    private static List<Move> moveHistory = new ArrayList<Move>();
    private static Deck deck;
    private CardObserver cardObserver;

    /**
     * Gets the number of players and deals to all players.
     * Assumes a minimum of two players
     *
     * @param players players to add
     */
    public Game(Player[] players){
        Game.players = players;
        deck = new Deck();

        for (int i = 0; i < Game.players.length; i++) {
            Game.players[i].addCardsToHand(deck.deal(4));
        }
    }//end constructor

    /**
     *  The meat of the game.
     *
     *  move history
     *  points of all players
     */
    public void gameLoop(){

        int roundCount = 0;

        do {// Main loop
            for(int i = 0; i <= (players.length - 1) ; i++){
                // Deal cards
                players[i].addCardsToHand(deck.deal(1));
                updateHandObserver();
                updateDeckObserver();

                Move playerMove = players[i].makeMove();          //player makes the move
                processMove(playerMove);
                updateHandObserver();
                updateHouseObserver();

                moveHistory.add(playerMove);                    //adds the move to our move history for stats
            }//end for all players

            System.out.println("ROUND " + roundCount++);
        } while( ! gameOver());

        // TODO process stats here
    }

    private void processMove(Move move) {
        if (move.getReceivingPlayerID() == Move.DISCARD_PILE) {
            deck.discard(move.getCard());
            updateDiscardPileObserver();

            for (Card card : Game.getPlayer(move.getCardPlayerID()).getHand()) {
                System.out.print(card + ", ");
            }
            System.out.print(move + "\n");
        } else {
            players[move.getReceivingPlayerID()].setCardInHouse(move);
        }
        System.out.println(move);
    }

    /**
     *  If the deck is empty and no moves are left end the game.
     *
     * @return  true if all players have skipped their turn and the deck is empty
     */
    private boolean gameOver() {
        if(deck.size() == 0){
            for (int i = 0; i < players.length; i++) {
                if ( ! Move.skipped(moveHistory.get(moveHistory.size() - i - 1))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }//end allPlayersPassed

    /**
     * Returns the player with the given id.
     *
     * @param id    id of desired player
     * @return      player with matching id if found
     */
    public static Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getID() == id) {
                return player;
            }
        }
        return null;
    }

    public static Player[] getPlayers() {
        return players;
    }

    public static Player[] getPlayersExcept(int id) {

        Player[] returnPlayers = new Player[players.length -1];
        int offset = 0;

        for (int i = 0; i < players.length; i++) {
            if (players[i].getID() != id) {
                returnPlayers[offset] = players[i];
                offset++;
            }
        }

        return returnPlayers;
    }

    /**
     * Returns the game deck.
     * Can be used to check the contents of the deck or discard pile.
     *
     * @return game deck
     */
    public static Deck getDeck() {
        return deck;
    }

    /**
     * Returns the game's move history.
     * This is the complete history of the current game session.
     *
     * @return game move history
     */
    public static List<Move> getMoveHistory() {
        return moveHistory;
    }

    /*
     | ------------------------------------------
     | Observer Methods
     | ------------------------------------------
     |
     | Everything to do with updating the game observers (view) goes here.
     */

    /**
     * Sets the card observer and syncs it.
     *
     * @param cardObserver view that observes the cards
     */
    public void setCardObserver(CardObserver cardObserver) {
        this.cardObserver = cardObserver;
        updateAllObservers();
    }

    /**
     * Updates every observer
     */
    private void updateAllObservers() {
        updateDeckObserver();
        updateHandObserver();
        updateHouseObserver();
        updateDiscardPileObserver();
    }

    private void updateHandObserver() {
        if (cardObserver != null)
            cardObserver.updateHands();
    }

    private void updateHouseObserver() {
        if (cardObserver != null)
            cardObserver.updateHouses();
    }

    private void updateDeckObserver() {
        if (cardObserver != null)
            cardObserver.updateDeck();
    }

    private void updateDiscardPileObserver() {
        if (cardObserver != null)
            cardObserver.updateDiscardPile();
    }

    public static void main(String[] args){

        Player[] players = new Player[] {
                new Bot(0),
                new Bot(1),
                new Bot(2),
                new Bot(3)
        };

        Game game = new Game(players);

        System.out.println("Cards have been dealt.");

        game.gameLoop();
    }
}//end Game Class
