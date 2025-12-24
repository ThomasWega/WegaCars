package me.wega.cars.vehicle.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VehicleAlignmentCombination {
    NO_TOE_NO_CAMBER("No Toe, No Camber", "notoe_nocamber", "notoe_nocamber"),
    TOE_IN_NO_CAMBER("Toe In, No Camber", "toein_nocamber", "toeout_nocamber"),
    TOE_OUT_NO_CAMBER("Toe Out, No Camber", "toeout_nocamber", "toein_nocamber"),
    NO_TOE_POSITIVE_CAMBER("No Toe, Positive Camber", "notoe_positivecamber", "notoe_negativecamber"),
    TOE_IN_POSITIVE_CAMBER("Toe In, Positive Camber", "toein_positivecamber", "toeout_negativecamber"),
    TOE_OUT_POSITIVE_CAMBER("Toe Out, Positive Camber", "toeout_positivecamber", "toein_negativecamber"),
    NO_TOE_NEGATIVE_CAMBER("No Toe, Negative Camber", "notoe_negativecamber", "notoe_positivecamber"),
    TOE_IN_NEGATIVE_CAMBER("Toe In, Negative Camber", "toein_negativecamber", "toeout_positivecamber"),
    TOE_OUT_NEGATIVE_CAMBER("Toe Out, Negative Camber", "toeout_negativecamber", "toein_positivecamber");

    private final String name;
    private final String leftModel;
    private final String rightModel;

}
