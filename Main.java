import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Main {
    private final JFrame frame;
    private final JList<String> pokemonList;
    private List<Pokemon> pokemons;

    public Main() {
        frame = new JFrame("Pokemon List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        pokemonList = new JList<>();
        frame.add(new JScrollPane(pokemonList), BorderLayout.CENTER);

        pokemonList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = pokemonList.getSelectedIndex();
                if (selectedIndex != -1) {
                    showPokemonDetails(pokemons.get(selectedIndex));
                }
            }
        });
    }

    public void show() {
        frame.setVisible(true);
    }

    public void updatePokemonList(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Pokemon pokemon : pokemons) {
            listModel.addElement(pokemon.name);
        }
        pokemonList.setModel(listModel);
    }

    private void showPokemonDetails(Pokemon pokemon) {
        try {
            PokemonDetailsApiClient detailsClient = new PokemonDetailsApiClient();
            Pokemon detailedPokemon = detailsClient.fetchPokemonDetails(pokemon.url);

            StringBuilder types = new StringBuilder();
            if (detailedPokemon.types != null) {
                for (Pokemon.Type type : detailedPokemon.types) {
                    types.append(type.type.name).append(" ");
                }
            }

            String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                    + detailedPokemon.id + ".png";

            Icon icon = null;
            if (imageUrl != null) {
                try {
                    icon = new ImageIcon(new ImageIcon(new URL(imageUrl))
                            .getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(frame,
                    "Name: " + detailedPokemon.name +
                            "\nTypes: " + types.toString().trim() +
                            "\nURL: " + pokemon.url,
                    "Pokemon Details",
                    JOptionPane.INFORMATION_MESSAGE,
                    icon);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Failed to fetch Pokemon details", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.show();

            PokemonApiClient fetcher = new PokemonApiClient();
            try {
                List<Pokemon> pokemons = fetcher.fetchPokemon();
                app.updatePokemonList(pokemons);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(app.frame, "Failed to fetch Pokemon data", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}