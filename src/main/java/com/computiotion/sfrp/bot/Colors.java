package com.computiotion.sfrp.bot;

import java.awt.*;

public enum Colors {
    Default(Color.BLACK),
    White(Color.WHITE),
    Aqua(new Color(26, 188, 156)),
    Green(new Color(87, 242, 136)),
    Blue(new Color(52, 152, 219)),
    Yellow(new Color(254, 230, 92)),
    Purple(new Color(156, 89, 182)),
    LuminousVividPink(new Color(233, 30, 98)),
    Fuchsia(new Color(235, 69, 158)),
    Gold(new Color(241, 196, 15)),
    Orange(new Color(230, 125, 34)),
    Red(new Color(237, 66, 69)),
    Grey(new Color(149, 165, 166)),
    Navy(new Color(52, 73, 94)),
    DarkAqua(new Color(17, 128, 106)),
    DarkGreen(new Color(31, 139, 76)),
    DarkBlue(new Color(206694)),
    DarkPurple(new Color(113, 54, 138)),
    DarkVividPink(new Color(173, 20, 87)),
    DarkGold(new Color(194, 125, 14)),
    DarkOrange(new Color(168, 67, 0)),
    DarkRed(new Color(153, 46, 34)),
    DarkGrey(new Color(151, 156, 159)),
    DarkerGrey(new Color(127, 140, 141)),
    LightGrey(new Color(188, 192, 192)),
    DarkNavy(new Color(44, 62, 80)),
    Blurple(new Color(88, 101, 242)),
    Greyple(new Color(153, 170, 181)),
    DarkButNotBlack(new Color(44, 47, 51)),
    NotQuiteBlack(new Color(35, 39, 42)),
    DarkMode(new Color(0x2b2d31)),
    AssistanceColor(new Color(93,95,95)); // christmas colors
//    AssistanceColor(new Color(62, 33, 205)); // regular colors
//    AssistanceColor(new Color(255, 183, 0)); // halloween colors

    private final Color color;

    Colors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
