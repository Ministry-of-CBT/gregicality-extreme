package gregicadditions.machines.multi.simple;

import gregicadditions.GAConfig;
import gregicadditions.item.components.MotorCasing;
import gregicadditions.item.components.PumpCasing;
import gregicadditions.item.metal.MetalCasing1;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.multiblock.BlockPattern;
import gregtech.api.multiblock.FactoryBlockPattern;
import gregtech.api.multiblock.PatternMatchContext;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.render.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static gregicadditions.client.ClientHandler.*;
import static gregicadditions.item.GAMetaBlocks.METAL_CASING_1;

public class TileEntityLargeElectrolyzer extends LargeSimpleRecipeMapMultiblockController {

	private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.IMPORT_FLUIDS, MultiblockAbility.EXPORT_FLUIDS, MultiblockAbility.INPUT_ENERGY, MultiblockAbility.MAINTENANCE_HATCH};


	public TileEntityLargeElectrolyzer(ResourceLocation metaTileEntityId) {
		super(metaTileEntityId, RecipeMaps.ELECTROLYZER_RECIPES, GAConfig.multis.largeElectrolyzer.euPercentage, GAConfig.multis.largeElectrolyzer.durationPercentage, GAConfig.multis.largeElectrolyzer.chancedBoostPercentage, GAConfig.multis.largeElectrolyzer.stack);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new TileEntityLargeElectrolyzer(metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("XXCXX", "XXCXX", "XXCXX", "XX#XX")
				.aisle("XXCXX", "XP#MX", "XXCXX", "X###X").setRepeatable(1, 6)
				.aisle("XXXXX", "XXSXX", "XXCXX", "XX#XX")
				.setAmountAtLeast('L', 12)
				.where('S', selfPredicate())
				.where('L', statePredicate(casingState))
				.where('X', statePredicate(casingState).or(abilityPartPredicate(ALLOWED_ABILITIES)))
				.where('C', statePredicate(MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE)))
				.where('#', isAirPredicate())
				.where('M', motorPredicate())
				.where('P', pumpPredicate())
				.build();
	}

	@Override
	protected void formStructure(PatternMatchContext context) {
		super.formStructure(context);
		MotorCasing.CasingType motor = context.getOrDefault("Motor", MotorCasing.CasingType.MOTOR_LV);
		PumpCasing.CasingType pump = context.getOrDefault("Pump", PumpCasing.CasingType.PUMP_LV);
		int min = Math.min(motor.getTier(), pump.getTier());
		maxVoltage = (long) (Math.pow(4, min) * 8);
	}

	private static final IBlockState casingState = METAL_CASING_1.getState(MetalCasing1.CasingType.TALONITE);

	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
		return TALONITE_CASING;
	}

	@Nonnull
	@Override
	protected OrientedOverlayRenderer getFrontOverlay() {
		return Textures.ELECTROLYZER_OVERLAY;
	}
}
