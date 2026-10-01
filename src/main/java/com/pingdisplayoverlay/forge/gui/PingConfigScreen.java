package com.pingdisplayoverlay.forge.gui;

import com.pingdisplayoverlay.forge.config.PingConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class PingConfigScreen extends Screen {
    private enum Tab { GENERAL, TAB, HUD }

    private static final int LABEL_WIDTH = 200;
    private static final int GAP = 8;
    private static final int VALUE_WIDTH = 90;
    private static final int RESET_WIDTH = 60;
    private static final int ROW_HEIGHT = 24;
    private static final int CONTENT_TOP = 60;
    private static final int ON_COLOR = 0x55C94B;
    private static final int OFF_COLOR = 0xE53935;

    private final @Nullable Screen parent;
    private final PingConfig cfg = PingConfig.get();
    private final PingConfig defaults = PingConfig.DEFAULTS;
    private Tab currentTab = Tab.GENERAL;

    public PingConfigScreen(@Nullable Screen parent) {
        super(Component.translatable("pingdisplayoverlay.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int titleWidth = this.font.width(this.title);
        addRenderableWidget(new StringWidget((this.width - titleWidth) / 2, 12, titleWidth, this.font.lineHeight, this.title, this.font));

        int tabButtonWidth = 100;
        int tabsTotal = tabButtonWidth * 3 + 8;
        int tabX = this.width / 2 - tabsTotal / 2;
        addTabButton(tabX, Tab.GENERAL, "pingdisplayoverlay.config.tab.general", tabButtonWidth);
        tabX += tabButtonWidth + 4;
        addTabButton(tabX, Tab.TAB, "pingdisplayoverlay.config.tab.tab", tabButtonWidth);
        tabX += tabButtonWidth + 4;
        addTabButton(tabX, Tab.HUD, "pingdisplayoverlay.config.tab.hud", tabButtonWidth);

        addRenderableWidget(Button.builder(Component.translatable("pingdisplayoverlay.config.done"), b -> onDone())
            .bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());

        buildTabContent();
    }

    private void addTabButton(int x, Tab tab, String key, int width) {
        Button btn = Button.builder(Component.translatable(key), b -> switchTab(tab)).bounds(x, 32, width, 20).build();
        btn.active = this.currentTab != tab;
        addRenderableWidget(btn);
    }

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        this.clearWidgets();
        this.init();
    }

    private void onDone() {
        cfg.save();
        this.minecraft.setScreenAndShow(this.parent);
    }

    private void buildTabContent() {
        int center = this.width / 2;
        int totalWidth = LABEL_WIDTH + GAP + VALUE_WIDTH + GAP + RESET_WIDTH;
        int left = center - totalWidth / 2;
        int valueX = left + LABEL_WIDTH + GAP;
        int resetX = valueX + VALUE_WIDTH + GAP;
        int y = CONTENT_TOP;

        switch (currentTab) {
            case GENERAL -> {
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.threshold_very_good", cfg.thresholdVeryGood, defaults.thresholdVeryGood, v -> cfg.thresholdVeryGood = v, null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.threshold_good", cfg.thresholdGood, defaults.thresholdGood, v -> cfg.thresholdGood = v, null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.threshold_ok", cfg.thresholdOk, defaults.thresholdOk, v -> cfg.thresholdOk = v, null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.threshold_bad", cfg.thresholdBad, defaults.thresholdBad, v -> cfg.thresholdBad = v, null);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_very_good", cfg.colorVeryGood, defaults.colorVeryGood, v -> cfg.colorVeryGood = v);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_good", cfg.colorGood, defaults.colorGood, v -> cfg.colorGood = v);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_ok", cfg.colorOk, defaults.colorOk, v -> cfg.colorOk = v);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_bad", cfg.colorBad, defaults.colorBad, v -> cfg.colorBad = v);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_very_bad", cfg.colorVeryBad, defaults.colorVeryBad, v -> cfg.colorVeryBad = v);
                y = colorField(left, valueX, resetX, y, "pingdisplayoverlay.config.color_lost", cfg.colorLost, defaults.colorLost, v -> cfg.colorLost = v);
            }
            case TAB -> {
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.enabled", cfg.tabEnabled, defaults.tabEnabled, v -> cfg.tabEnabled = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_ping", cfg.tabShowPing, defaults.tabShowPing, v -> cfg.tabShowPing = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_indicator", cfg.tabShowIndicator, defaults.tabShowIndicator, v -> cfg.tabShowIndicator = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_heads", cfg.tabShowHeads, defaults.tabShowHeads, v -> cfg.tabShowHeads = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_hat", cfg.tabShowHat, defaults.tabShowHat, v -> cfg.tabShowHat = v);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.head_opacity", cfg.tabHeadOpacity, defaults.tabHeadOpacity, v -> cfg.tabHeadOpacity = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.ping_opacity", cfg.tabPingOpacity, defaults.tabPingOpacity, v -> cfg.tabPingOpacity = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.tab_bg_opacity", cfg.tabBackgroundOpacity, defaults.tabBackgroundOpacity, v -> cfg.tabBackgroundOpacity = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.tab_width", cfg.tabWidth, defaults.tabWidth, v -> cfg.tabWidth = v, "pingdisplayoverlay.config.tab_width.tooltip");
            }
            case HUD -> {
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.enabled", cfg.hudEnabled, defaults.hudEnabled, v -> cfg.hudEnabled = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_ping", cfg.hudShowPing, defaults.hudShowPing, v -> cfg.hudShowPing = v);
                y = boolField(left, valueX, resetX, y, "pingdisplayoverlay.config.show_indicator", cfg.hudShowIndicator, defaults.hudShowIndicator, v -> cfg.hudShowIndicator = v);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.hud_x", cfg.hudX, defaults.hudX, v -> cfg.hudX = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.hud_y", cfg.hudY, defaults.hudY, v -> cfg.hudY = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.ping_opacity", cfg.hudPingOpacity, defaults.hudPingOpacity, v -> cfg.hudPingOpacity = clampPct(v), null);
                y = intField(left, valueX, resetX, y, "pingdisplayoverlay.config.bg_opacity", cfg.hudBackgroundOpacity, defaults.hudBackgroundOpacity, v -> cfg.hudBackgroundOpacity = clampPct(v), null);
            }
        }
    }

    private static int clampPct(int v) {
        return Math.max(0, Math.min(100, v));
    }

    private interface IntSetter { void set(int v); }
    private interface BoolSetter { void set(boolean v); }

    private int intField(int left, int valueX, int resetX, int y, String key, int value, int defaultValue, IntSetter setter, @Nullable String tooltipKey) {
        final int rowTop = y;
        final int rowH = pdoRowHeight(key);
        y = rowTop + (rowH - 20) / 2;
        MultiLineTextWidget label = pdoLabel(left, rowTop, rowH, key);
        if (tooltipKey != null) {
            label.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
        }
        addRenderableWidget(label);

        EditBox box = new EditBox(this.font, valueX, y, VALUE_WIDTH, 20, Component.translatable(key));
        box.setValue(Integer.toString(value));
        box.setMaxLength(8);
        if (tooltipKey != null) {
            box.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
        }
        addRenderableWidget(box);

        Button reset = Button.builder(Component.translatable("pingdisplayoverlay.config.reset"), b -> {
            setter.set(defaultValue);
            box.setValue(Integer.toString(defaultValue));
            b.active = false;
        }).bounds(resetX, y, RESET_WIDTH, 20).build();
        reset.active = value != defaultValue;
        addRenderableWidget(reset);

        box.setResponder(text -> {
            try {
                int v = Integer.parseInt(text.trim());
                setter.set(v);
                reset.active = v != defaultValue;
            } catch (NumberFormatException ignored) {
            }
        });
        return rowTop + rowH + 4;
    }

    private int colorField(int left, int valueX, int resetX, int y, String key, int value, int defaultValue, IntSetter setter) {
        final int rowTop = y;
        final int rowH = pdoRowHeight(key);
        y = rowTop + (rowH - 20) / 2;
        addRenderableWidget(pdoLabel(left, rowTop, rowH, key));

        EditBox box = new EditBox(this.font, valueX, y, VALUE_WIDTH, 20, Component.translatable(key));
        box.setValue(String.format("%06X", value & 0xFFFFFF));
        box.setMaxLength(6);
        addRenderableWidget(box);

        Button reset = Button.builder(Component.translatable("pingdisplayoverlay.config.reset"), b -> {
            setter.set(defaultValue);
            box.setValue(String.format("%06X", defaultValue & 0xFFFFFF));
            b.active = false;
        }).bounds(resetX, y, RESET_WIDTH, 20).build();
        reset.active = value != defaultValue;
        addRenderableWidget(reset);

        box.setResponder(text -> {
            try {
                int v = Integer.parseInt(text.trim(), 16);
                setter.set(v);
                reset.active = v != defaultValue;
            } catch (NumberFormatException ignored) {
            }
        });
        return rowTop + rowH + 4;
    }

    private static Component boolLabel(boolean v) {
        String key = v ? "pingdisplayoverlay.config.on" : "pingdisplayoverlay.config.off";
        int color = v ? ON_COLOR : OFF_COLOR;
        return Component.translatable(key).copy().withStyle(style -> style.withColor(color));
    }

    private int boolField(int left, int valueX, int resetX, int y, String key, boolean initial, boolean defaultValue, BoolSetter setter) {
        final int rowTop = y;
        final int rowH = pdoRowHeight(key);
        y = rowTop + (rowH - 20) / 2;
        addRenderableWidget(pdoLabel(left, rowTop, rowH, key));

        final boolean[] state = { initial };
        final Button[] holder = new Button[1];
        Button reset = Button.builder(Component.translatable("pingdisplayoverlay.config.reset"), b -> {
            state[0] = defaultValue;
            setter.set(defaultValue);
            holder[0].setMessage(boolLabel(defaultValue));
            b.active = false;
        }).bounds(resetX, y, RESET_WIDTH, 20).build();
        reset.active = initial != defaultValue;

        Button btn = Button.builder(boolLabel(initial), b -> {
            state[0] = !state[0];
            setter.set(state[0]);
            holder[0].setMessage(boolLabel(state[0]));
            reset.active = state[0] != defaultValue;
        }).bounds(valueX, y, VALUE_WIDTH, 20).build();
        holder[0] = btn;
        addRenderableWidget(btn);
        addRenderableWidget(reset);
        return rowTop + rowH + 4;
    }

    /** Height of a row: tall enough for the wrapped label (min. one widget height). */
    private int pdoRowHeight(String key) {
        return Math.max(20, new MultiLineTextWidget(Component.translatable(key), this.font).setMaxWidth(LABEL_WIDTH).getHeight());
    }

    /** Wrapped label, vertically centered inside the row. */
    private MultiLineTextWidget pdoLabel(int left, int rowTop, int rowH, String key) {
        MultiLineTextWidget w = new MultiLineTextWidget(Component.translatable(key), this.font).setMaxWidth(LABEL_WIDTH);
        w.setPosition(left, rowTop + (rowH - w.getHeight()) / 2);
        return w;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
