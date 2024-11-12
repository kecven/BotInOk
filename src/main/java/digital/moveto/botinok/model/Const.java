package digital.moveto.botinok.model;

import org.modelmapper.ModelMapper;

public class Const {
    public static final ModelMapper modelMapper = initModelMapper();

    public static final String DEFAULT_LOCATION = "ISRAEL";
    public static final String SEPARATOR_FOR_LOCATIONS = " \\| ";

    public static final String VERSION = "0.2.7";

    private static ModelMapper initModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
