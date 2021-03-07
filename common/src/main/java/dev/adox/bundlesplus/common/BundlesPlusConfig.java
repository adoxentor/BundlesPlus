package dev.adox.bundlesplus.common;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "bundlesplus")
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class BundlesPlusConfig implements ConfigData {

    public enum StackMode {
        All1,
        UpTo16,
        Vanilla
    }

    public enum Button {
        Left(0),
        Right(1),
        Middle(2),
        Four(3),
        Five(4);
        public final int value;

        Button(int value) {
            this.value = value;
        }
    }


    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public StackMode STACK_MODE = StackMode.UpTo16;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public Button BUTTON = Button.Right;

    public Boolean TOOLTIP = false;
}
