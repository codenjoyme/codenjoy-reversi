package com.codenjoy.dojo.reversi.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2012 - 2022 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.reversi.TestGameSettings;
import com.codenjoy.dojo.reversi.services.Event;
import com.codenjoy.dojo.reversi.services.GameSettings;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.dice.MockDice;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static com.codenjoy.dojo.reversi.services.GameSettings.Keys.LEVEL_MAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GameTest {

    private Reversi game;
    private Hero hero1;
    private Hero hero2;
    private MockDice dice;
    private EventListener listener1;
    private EventListener listener2;
    private Player player1;
    private Player player2;
    private PrinterFactory printer;
    private GameSettings settings;

    @Before
    public void setup() {
        dice = new MockDice();
        printer = new PrinterFactoryImpl();
        settings = new TestGameSettings();
    }

    private void dice(Integer... next) {
        dice.then(next);
    }

    private void givenFl(String board) {
        settings.string(LEVEL_MAP, board);
        game = new Reversi(settings.level(), dice, settings);
        listener1 = mock(EventListener.class);
        listener2 = mock(EventListener.class);
        player1 = new Player(listener1, settings);
        player2 = new Player(listener2, settings);
        game.newGame(player1);
        game.newGame(player2);
        hero1 = game.getHeroes().get(0);
        hero2 = game.getHeroes().get(1);
    }

    private void assertE(String expected, Player player) {
        Object actualOne = printer.getPrinter(game.reader(), player).print();
        String expectedOne = TestUtils.injectN(expected);
        assertEquals(expectedOne, actualOne);

        assertE2(expectedOne, player);
    }

    private void assertE2(String expectedOne, Player player) {
        if (game.getHeroes().size() != 2) {
            return;
        }

        Object actualAnother = printer.getPrinter(game.reader(), another(player)).print();
        String expectedAnother = expectedOne.replaceAll("x", "+").replaceAll("o", ".");

        assertEquals(expectedAnother, actualAnother);
    }

    private GamePlayer another(Player player) {
        return (player == player1) ? player2 : player1;
    }

    // ???????? ???????????????????? ?????????????????????????????????? 4 ?????????????? ???? 2 ???? ???????????? ????????
    @Test
    public void shouldFieldAtStart() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        assertE("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ",
                player1);

        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????? ?? ?????????????????????? ???????? ?????????? ?????????????????????????? ??????????
    @Test
    public void shouldChangeChip_left() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(5, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????? ?? ?????????????????????? ???????? ?????????? ?????????????????????????? ????????????
    @Test
    public void shouldChangeChip_right() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(2, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "  ooo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????? ?? ?????????????????????? ???????? ?????????? ?????????????????????? ??????????
    @Test
    public void shouldChangeChip_up() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(4, 2);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oo   " +
                "    o   " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????? ?? ?????????????????????? ???????? ?????????? ?????????????????????? ????????
    @Test
    public void shouldChangeChip_down() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(3, 5);
        game.tick();

        assertE("        " +
                "        " +
                "   o    " +
                "   oo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?? ???? ???????? ???????????????? ???????? ???????????? ???? ?????????????????? (????????)
    @Test
    public void shouldNotChangeChip_whenNoEnemy_down() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(4, 5);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????????????? ???????????????????????????? ?? ???????????????????? ????????????????????????
    @Test
    public void shouldChangeChipsInAllDirections() {
        givenFl("x       " +
                "O   O   " +
                "    x   " +
                "  Ox xO " +
                "    x   " +
                "    O   " +
                "        " +
                "        ");

        hero1.act(4, 4);
        game.tick();

        assertE("X       " +
                "o   o   " +
                "    o   " +
                "  ooooo " +
                "    o   " +
                "    o   " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(4));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????????????????? ???????????? ?? ?????? ???????????????????????? ?????? ???????? ?????? ?????????? ?????????? 1
    @Test
    public void shouldNotChangeChips_whenNoSupportChip() {
        givenFl("        " +
                "    O   " +
                "    x   " +
                "   x x  " +
                "    x   " +
                "        " +
                "        " +
                "        ");

        hero1.act(4, 4);
        game.tick();

        assertE("        " +
                "    o   " +
                "    o   " +
                "   XoX  " +
                "    X   " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ?????????????????????? ?????????????????? ?????????? ?????????? ?? ?????????? ??????????????????????
    @Test
    public void shouldChangeSeveralChips_whenSupportChipInOtherSide() {
        givenFl("x   O   " +
                "O   x   " +
                "    x   " +
                "Oxxx xxO" +
                "    x   " +
                "    x   " +
                "    x   " +
                "    O   ");

        hero1.act(4, 4);
        game.tick();

        assertE("X   o   " +
                "o   o   " +
                "    o   " +
                "oooooooo" +
                "    o   " +
                "    o   " +
                "    o   " +
                "    o   ",
                player2);

        verify(listener1).event(Event.FLIP.apply(10));
        verifyNoMoreInteractions();
    }

    // ?? ???? ???????? ?????????????????????? ?????????????????? ?????????? ?????????? ?? ?????????? ?????????????????????? ???????? ?? ???????? ??????
    // ?????????? ????????????????
    @Test
    public void shouldNotChangeSeveralChips_whenNoSupportChipInOtherSide_white() {
        givenFl("O       " +
                "x   x   " +
                "    x   " +
                " xxx xx " +
                "    x   " +
                "    x   " +
                "    x   " +
                "        ");

        hero1.act(4, 4);
        game.tick();

        assertE("O       " +
                "x   x   " +
                "    x   " +
                " xxx xx " +
                "    x   " +
                "    x   " +
                "    x   " +
                "        ",
                player1);

        verifyNoMoreInteractions();
    }

    @Test
    public void shouldNotChangeSeveralChips_whenNoSupportChipInOtherSide_black() {
        givenFl("X       " +
                "o   o   " +
                "    o   " +
                " ooo oo " +
                "    o   " +
                "    o   " +
                "    o   " +
                "        ");

        hero2.act(4, 4);
        game.tick();

        assertE("X       " +
                "o   o   " +
                "    o   " +
                " ooo oo " +
                "    o   " +
                "    o   " +
                "    o   " +
                "        ",
                player2);

        verifyNoMoreInteractions();
    }

    // ?? ???????? ?????????????????????? ?????????????????? ?????????? ?????????? ?? ?????????? ?????????????????????? ????????????
    // ???????? ?? ???????? ???????? ?????????? ????????????????
    @Test
    public void shouldChangeSeveralChips_whenSupportChipInOtherSide_case2() {
        givenFl("    O   " +
                "    x   " +
                "    x   " +
                "Oxxx xx " +
                "    x   " +
                "    x   " +
                "    x   " +
                "        ");

        hero1.act(4, 4);
        game.tick();

        assertE("    o   " +
                "    o   " +
                "    o   " +
                "oooooXX " +
                "    X   " +
                "    X   " +
                "    X   " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(5));
        verifyNoMoreInteractions();
    }

    // ?? ???????? ???????????????? ?? ?????????????????????? ???????? ?????????? ?????????????????????? ?? ???????????? ??????????????
    @Test
    public void shouldChangeSeveralChips_whenWhenDiagonalDirections() {
        givenFl("x      O" +
                "O O   x " +
                "   x x  " +
                "        " +
                "   x x  " +
                "  x   O " +
                " x      " +
                "O       ");

        hero1.act(4, 4);
        game.tick();

        assertE("X      o" +
                "o o   o " +
                "   o o  " +
                "    o   " +
                "   o o  " +
                "  o   o " +
                " o      " +
                "o       ",
                player2);

        verify(listener1).event(Event.FLIP.apply(7));
        verifyNoMoreInteractions();
    }

    // ?????????????????????? ???????????????? ?????????????? ???????????? - ???? ??????????????????
    // ?? ?????????? ???????????? - ????????????????????
    // ???? ???????? ???????? ???? ?????????????? ?????????????????? ??????, ???? ?????????????????? ?????????? ?????? ??????????????
    @Test
    public void shouldChangeChip_blackGoesFirst() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero2.act(5, 4);
        game.tick();

        verifyNoMoreInteractions();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        hero1.act(5, 3);
        hero2.act(5, 4);
        game.tick();

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        assertE("        " +
                "        " +
                "        " +
                "   xxx  " +
                "   Ox   " +
                "        " +
                "        " +
                "        ",
                player1);

        hero1.act(5, 3);
        game.tick();

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        assertE("        " +
                "        " +
                "        " +
                "   XXX  " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    // ?????????????????? ??????????, ?????????? ?????? ???????? ???????????? ????
    @Test
    public void shouldWinLose_noEmptySpace_whiteWin() {
        givenFl("oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oXoooooo" +
                "  oXoooo");

        hero2.act(1, 0);
        game.tick();

        assertE("OOOOOOOO" +
                "OOOOOOOO" +
                "OOOOOOOO" +
                "OOOOOOOO" +
                "OOOOOOOO" +
                "OOOOOOOO" +
                "OxOOOOOO" +
                " xxxOOOO",
                player1);


        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero1.act(0, 0);
        game.tick();

        assertE("oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo",
                player2);

        verify(listener1).event(Event.FLIP.apply(4));
        verify(listener1).event(Event.WIN);
        verify(listener2).event(Event.LOSE);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oooooooo" +
                "oXoooooo" +
                "  oXoooo",
                player2);
    }
    
    // ?????????????????? ????????????, ?????????? ?????? ???????? ???????????? ????
    @Test
    public void shouldWinLose_noEmptySpace_blackWin() {
        givenFl("xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xOxxxxxx" +
                "  xOxxxx");

        hero1.act(1, 0);
        game.tick();

        assertE("XXXXXXXX" +
                "XXXXXXXX" +
                "XXXXXXXX" +
                "XXXXXXXX" +
                "XXXXXXXX" +
                "XXXXXXXX" +
                "XoXXXXXX" +
                " oooXXXX",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero2.act(0, 0);
        game.tick();

        assertE("xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx",
                player1);

        verify(listener1).event(Event.LOSE);
        verify(listener2).event(Event.FLIP.apply(4));
        verify(listener2).event(Event.WIN);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xxxxxxxx" +
                "xOxxxxxx" +
                "  xOxxxx",
                player1);
    }

    // ?????????????????? ??????????, ?????????? ???? ???????????????? ?????????? ?? ??????????????
    @Test
    public void shouldWinLose_noBlackChip_whiteWin() {
        givenFl("        " +
                "        " +
                "        " +
                "  oXo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ");

        hero2.act(1, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                " xxxO   " +
                "   OOO  " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero1.act(0, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "ooooo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(3));
        verify(listener1).event(Event.WIN);
        verify(listener2).event(Event.LOSE);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("        " +
                "        " +
                "        " +
                "  oXo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    // ?????????????????? ????????????, ?????????? ???? ???????????????? ?????????? ?? ????????????
    @Test
    public void shouldWinLose_noWhiteChip_blackWin() {
        givenFl("        " +
                "        " +
                "        " +
                "  xOx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ");

        hero1.act(1, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                " oooX   " +
                "   XXX  " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero2.act(0, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "xxxxx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener1).event(Event.LOSE);
        verify(listener2).event(Event.FLIP.apply(3));
        verify(listener2).event(Event.WIN);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("        " +
                "        " +
                "        " +
                "  xOx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ",
                player1);
    }

    // ?????????????????? ??????????, ?????????? ???? ???????????????? ???????? ???????????? ?????????? ?? ?? ?????????????? ?????????? ????????????
    @Test
    public void shouldWinLose_noBlackChipPossibleTurn_whiteWin() {
        givenFl("       X" +
                "        " +
                "        " +
                "  oXo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ");

        hero2.act(1, 4);
        game.tick();

        assertE("       x" +
                "        " +
                "        " +
                " xxxO   " +
                "   OOO  " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero1.act(0, 4);
        game.tick();

        assertE("       X" +
                "        " +
                "        " +
                "ooooo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);


        verify(listener1).event(Event.FLIP.apply(3));
        verify(listener1).event(Event.WIN);
        verify(listener2).event(Event.LOSE);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("       X" +
                "        " +
                "        " +
                "  oXo   " +
                "   ooo  " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    // ?????????????????? ????????????, ?????????? ???? ???????????????? ???????? ???????????? ?????????? ?? ?? ?????????????? ?????????? ????????????
    @Test
    public void shouldWinLose_noWhiteChipPossibleTurn_blackWin() {
        givenFl("       O" +
                "        " +
                "        " +
                "  xOx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ");

        hero1.act(1, 4);
        game.tick();

        assertE("       o" +
                "        " +
                "        " +
                " oooX   " +
                "   XXX  " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero2.act(0, 4);
        game.tick();

        assertE("       O" +
                "        " +
                "        " +
                "xxxxx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener1).event(Event.LOSE);
        verify(listener2).event(Event.FLIP.apply(3));
        verify(listener2).event(Event.WIN);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("       O" +
                "        " +
                "        " +
                "  xOx   " +
                "   xxx  " +
                "        " +
                "        " +
                "        ",
                player1);
    }

    // ?????????????? ?????????????????? ???? act(x, y)
    @Test
    public void shouldSkipNotValidActCommand_oneParamener() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(5);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    @Test
    public void shouldSkipNotValidActCommand_zerroParamener() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act();
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    @Test
    public void shouldSkipNotValidActCommand_twoInvalidCoordinates() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(8, -1);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    @Test
    public void shouldValidActCommand_threeParameners() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        hero1.act(2, 4, 2);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "  ooo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ???????? ???? ???????? (???????? ??????????????????), ???????? ???????? ????????????????, ???????? ???? ??????????????????
    // ?????????? ?????? ????????????
    @Test
    public void shouldDoNothing_ifOnlyOnePlayer_black() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ");

        game.remove(player2);

        hero1.act(2, 4);
        game.tick();


        assertE("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ",
                player1);

        game.newGame(player2);
        hero2 = player2.getHero();

        assertEquals(true, player2.isBlack());

        hero1.act(2, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "  ooo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        hero2.act(2, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "  OOO   " +
                "  xxx   " +
                "        " +
                "        " +
                "        ",
                player1);
    }

    @Test
    public void shouldDoNothing_ifOnlyOnePlayer_white() {
        givenFl("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ");

        game.remove(player1);

        hero2.act(2, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ",
                player2);

        game.newGame(player1);
        hero1 = player1.getHero();

        assertEquals(true, player1.isWhite());

        hero2.act(2, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   xO   " +
                "  xxx   " +
                "        " +
                "        " +
                "        ",
                player1);

        hero1.act(2, 4);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "  ooo   " +
                "  XXX   " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    // ???????? ?????????????????? ???????????? ??????????, ???? ???????????????? ???????????????????? ?????? ???????????????????? ????????????
    @Test
    public void shouldDoNothing_whenAddThirdPlayer() {
        givenFl("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX   " +
                "        " +
                "        " +
                "        ");

        try {
            game.newGame(new Player(mock(EventListener.class), settings));
            fail("Expected exception");
        } catch (IllegalArgumentException exception) {
            assertEquals("???????????? ???????????????? ???????????? - ???????? ????????????!", exception.getMessage());
        }

        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox   " +
                "        " +
                "        " +
                "        ",
                player1);
    }

    // ?????????? ?????????? ???????? ???????????????? ?????? ???????????????? ??????????????
    @Test
    public void shouldSmallerBoard() {
        givenFl("    " +
                " xO " +
                " Ox " +
                "    ");

        assertEquals(4, game.size());

        hero1.act(3, 1);
        game.tick();

        assertE("    " +
                " Xo " +
                " ooo" +
                "    ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    @Test
    public void shouldBiggerBoard() {
        givenFl("            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "     xO     " +
                "     Ox     " +
                "            " +
                "            " +
                "            " +
                "            " +
                "            ");

        assertEquals(12, game.size());

        hero1.act(7, 5);
        game.tick();

        assertE("            " +
                "            " +
                "            " +
                "            " +
                "            " +
                "     Xo     " +
                "     ooo    " +
                "            " +
                "            " +
                "            " +
                "            " +
                "            ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?????????? ???? ???????????? ???????????????? ???? ??????????, ?????? ???????????????????? ??????????????
    @Test
    public void shouldSkipTurn_ifNoWay_white_atStart() {
        givenFl("   xx   " +
                "   OO   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        assertE("   XX   " +
                "   oo   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    // ???????????? ???? ???????????? ???????????????? ???? ??????????, ?????? ???????????????????? ????????????
    @Test
    public void shouldSkipTurn_ifNoWay_black_atStart() {
        givenFl("   oo   " +
                "   XX   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        assertE("   OO   " +
                "   xx   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);
    }

    // ?????????? ???????? ?????????????? ?? ???????????? ???? ???????????????? ?????????? ?? ???? ????????????????????
    @Test
    public void shouldSkipTurn_ifNoWay_white_afterBlack() {
        givenFl("  xOx   " +
                "   OO   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        hero1.act(5, 7);
        game.tick();

        assertE("  Xooo  " +
                "   oo   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero2.act(6, 7);
        game.tick();

        assertE("  XXXXX " +
                "   oo   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener2).event(Event.FLIP.apply(3));
        verifyNoMoreInteractions();

        hero2.act(2, 5);
        game.tick();

        assertE("  xxxxx " +
                "   xO   " +
                "  x     " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ?????????? ???????? ???????????? ?? ?????????????? ???? ???????????????? ?????????? ?? ???? ????????????????????
    @Test
    public void shouldSkipTurn_ifNoWay_black_afterWhite() {
        givenFl("  oXo   " +
                "   XX   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        hero2.act(5, 7);
        game.tick();

        assertE("  Oxxx  " +
                "   xx   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero1.act(6, 7);
        game.tick();

        assertE("  OOOOO " +
                "   xx   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener1).event(Event.FLIP.apply(3));
        verifyNoMoreInteractions();

        hero1.act(2, 5);
        game.tick();


        assertE("  ooooo " +
                "   oX   " +
                "  o     " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();
    }

    // ???????? ?????????????? ???????????????? ???? ????????????, ???? ????????????????????
    @Test
    public void shouldBadInputLevel() {

        try {
            givenFl("   xx   " +
                    "        " +
                    "        " +
                    "        " +
                    "        " +
                    "        " +
                    "        " +
                    "   OO   ");
            fail();
        } catch (IllegalArgumentException exception) {
            assertEquals("???????????????????? ?????????????? ????????????????", exception.getMessage());
        }
    }

    // ???????????????? ?????????????????????? ???? ????????
    // ???? ?????????????????????? ?????? ?????????????? ????????????
    @Test
    public void shouldCantSetChipOnBarrier_white() {
        givenFl("        " +
                "        " +
                "        " +
                "   xO   " +
                "   Ox???  " +
                "        " +
                "        " +
                "        ");

        hero1.act(5, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Xo   " +
                "   oX???  " +
                "        " +
                "        " +
                "        ",
                player2);

        verifyNoMoreInteractions();
    }

    @Test
    public void shouldCantSetChipOnBarrier_black() {
        givenFl("        " +
                "        " +
                "        " +
                "   oX   " +
                "   Xo???  " +
                "        " +
                "        " +
                "        ");

        hero2.act(5, 3);
        game.tick();

        assertE("        " +
                "        " +
                "        " +
                "   Ox   " +
                "   xO???  " +
                "        " +
                "        " +
                "        ",
                player1);

        verifyNoMoreInteractions();
    }

    // ?????????????????????? ???? ???????????????????? ?????? ?????????? ???????? ???????????????????????? ?????????? ?????????????????? ??????????
    // ?????????? ???????? ?????????????? ?? ???????????? ???? ???????????????? ?????????? ?? ???? ????????????????????
    @Test
    public void shouldFailIfNoTurnWithBreak_black() {
        givenFl("  oXo ??? " +
                "   XX   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        hero2.act(5, 7);
        game.tick();

        assertE("  oXXX??? " +
                "   XX   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener2).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero2.act(1, 7);
        game.tick();

        assertE(" xxxxx??? " +
                "   xx   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener1).event(Event.LOSE);
        verify(listener2).event(Event.FLIP.apply(1));
        verify(listener2).event(Event.WIN);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("  oXo ??? " +
                "   XX   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(listener1);
        Mockito.verifyNoMoreInteractions(listener2);
        Mockito.reset(listener1);
        Mockito.reset(listener2);
    }

    // ?????????????????????? ???? ???????????????????? ?????? ?????????? ???????? ???????????????????????? ?????????? ?????????????????? ??????????
    // ?????????? ???????? ???????????? ?? ?????????????? ???? ???????????????? ?????????? ?? ???? ????????????????????
    @Test
    public void shouldFailIfNoTurnWithBreak_white() {
        givenFl("  xOx ??? " +
                "   OO   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ");

        hero1.act(5, 7);
        game.tick();

        assertE("  xOOO??? " +
                "   OO   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);

        verify(listener1).event(Event.FLIP.apply(1));
        verifyNoMoreInteractions();

        hero1.act(1, 7);
        game.tick();

        assertE(" ooooo??? " +
                "   oo   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player2);

        verify(listener1).event(Event.FLIP.apply(1));
        verify(listener1).event(Event.WIN);
        verify(listener2).event(Event.LOSE);
        verifyNoMoreInteractions();

        game.tick(); // reset to new game

        assertE("  xOx ??? " +
                "   OO   " +
                "    ???   " +
                "        " +
                "        " +
                "        " +
                "        " +
                "        ",
                player1);
    }

}
