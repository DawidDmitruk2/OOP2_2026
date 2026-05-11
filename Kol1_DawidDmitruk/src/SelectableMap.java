// Klasa pochodna VoivodeshipMap, która umożliwia zaznaczenie jednego województwa
// i wyróżnienie go innym kolorem na mapie SVG.
public class SelectableMap extends VoivodeshipMap {

    // Nazwa aktualnie zaznaczonego województwa (null = żadne nie jest zaznaczone).
    private String selectedVoivodeship = null;

    // Ustawia województwo, które ma być wyróżnione na mapie.
    public void select(String voivodeship) {
        selectedVoivodeship = voivodeship;
    }

    // Nadpisuje metodę z klasy bazowej:
    // – zaznaczone województwo otrzymuje kolor "red" (czerwony),
    // – wszystkie pozostałe pozostają "black" (czarny).
    @Override
    protected String getColor(String voivodeship) {
        if (voivodeship.equals(selectedVoivodeship) == true) {
            return "red";
        }
        return "black";
    }

}
