package sdp.moneyrun.game;

import android.location.Location;

import java.util.List;

import sdp.moneyrun.map.Coin;
import sdp.moneyrun.map.Riddle;
import sdp.moneyrun.player.Player;

public class GameBuilder {
    private String name;
    private Player host;
    private List<Player> players;
    private int maxPlayerCount = 0;
    private List<Coin> coins;
    private List<Riddle> riddles;
    private Location startLocation;
    private boolean isVisible = true;

    /**
     * Empty constructor
     */
    public GameBuilder(){}

    /**
     * @param name the game name
     */
    public void setName(String name){
        if(name == null){
            throw new IllegalArgumentException("name should not be null.");
        }

        this.name = name;
    }

    /**
     * @param host the game host
     */
    public void setHost(Player host){
        if(host == null){
            throw new IllegalArgumentException("host should not be null.");
        }

        this.host = host;
    }

    /**
     * @param players the players in the game
     */
    public void setPlayers(List<Player> players){
        if(players == null){
            throw new IllegalArgumentException("players should not be null.");
        }
        if(players.isEmpty()){
            throw new IllegalArgumentException("players can never be empty (There should always be the host)");
        }

        this.players = players;
    }

    /**
     * @param maxPlayerCount the maximum number of players in the game
     */
    public void setMaxPlayerCount(int maxPlayerCount){
        if(maxPlayerCount <= 0){
            throw new IllegalArgumentException("max player count should be greater than 0.");
        }

        this.maxPlayerCount = maxPlayerCount;
    }

    /**
     * @param coins the coins located in the map for the game
     */
    public void setCoins(List<Coin> coins){
        if(coins == null){
            throw new IllegalArgumentException("coins should not be null.");
        }

        this.coins = coins;
    }

    /**
     * @param riddles the game riddles
     */
    public void setRiddles(List<Riddle> riddles){
        if(riddles == null){
            throw new IllegalArgumentException("riddles should not be null.");
        }

        this.riddles = riddles;
    }

    /**
     * @param startLocation the start location of the game
     */
    public void setStartLocation(Location startLocation){
        if(startLocation == null){
            throw new IllegalArgumentException("start location should not be null.");
        }

        this.startLocation = startLocation;
    }

    /**
     * @param isVisible The visibility of the game in the join list
     */
    public void setIsVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    public Game build(){
        if(name == null){
            throw new IllegalStateException("name should not be null.");
        }
        if(host == null){
            throw new IllegalStateException("host should not be null.");
        }
        if(maxPlayerCount <= 0){
            throw new IllegalStateException("max player count should be greater than 0.");
        }
        if(coins == null){
            throw new IllegalStateException("coins should not be null.");
        }
        if(startLocation == null){
            throw new IllegalStateException("start location should not be null.");
        }

        if(riddles == null){
            if(players == null){
                throw new IllegalStateException("players and riddles should not be null.");
            }

            return new Game(name, host, players, maxPlayerCount, startLocation, isVisible, coins);
        }

        Game game = new Game(name, host, maxPlayerCount, riddles, coins, startLocation, isVisible);
        if(players != null){
            game.setPlayers(players, true);
        }

        return game;
    }
}
