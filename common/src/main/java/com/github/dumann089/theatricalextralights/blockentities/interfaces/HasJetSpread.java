package com.github.dumann089.theatricalextralights.blockentities.interfaces;

public interface HasJetSpread {

    void setJetSpread(float x, float y, float z);

    float getJetSpreadX();
    float getJetSpreadY();
    float getJetSpreadZ();

    // Promedio de los tres ejes para compatibilidad con renderers antiguos
    default float getJetSpread() {
        return (getJetSpreadX() + getJetSpreadY() + getJetSpreadZ()) / 3.0f;
    }
}
