package djh.vilid.mixin;

import djh.vilid.Vilid;
import djh.vilid.ideology.politics.Ideology;
import djh.vilid.ideology.politics.Party;
import djh.vilid.item.custom.*;
import djh.vilid.ideology.politics.Viewpoint;
import djh.vilid.nation.NationData;
import djh.vilid.nation.NationUtil;
import djh.vilid.villager.ModProfessions;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

//import static djh.vilid.villager.ModActivities.createLearnTasks;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements VillagerExt{
	@Shadow protected abstract void sayNo();

	@Shadow public abstract VillagerData getVillagerData();

	//influence ideology based on new profession
//	@Inject(method = "setVillagerData", at = @At("HEAD"))
//	private void beforeVillagerDataChanged(VillagerData data, CallbackInfo ci) {
//		VillagerEntity villager = (VillagerEntity) (Object) this;
//		VillagerExt ext = (VillagerExt) villager;
//		VillagerData oldData = villager.getVillagerData();
//
//		if (!oldData.getProfession().equals(data.getProfession())){
//			//reset villager's ideology to their OG beliefs
//			ext.getViewpoint().resetIdeology();
//			//attempt to influence villager's politics by their new job's leanings
////			ext.getViewpoint().attemptInfluence(Ideology.jobWeights.get(data.getProfession()));
//		}
//	}



	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void onInteractMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		VillagerEntity villager = (VillagerEntity) (Object) this;
		VillagerExt ext = (VillagerExt) villager;
		ItemStack stack = player.getStackInHand(hand);

		// 1. Custom Items Take Priority (Propaganda & Polling)
		if (stack.getItem() instanceof ChangeNationItem || stack.getItem() instanceof BadItem ||
				stack.getItem() instanceof EducatorItem || stack.getItem() instanceof DumbItem ||
				stack.getItem() instanceof GoodItem || stack.getItem() instanceof LeftItem ||
				stack.getItem() instanceof RightItem || stack.getItem() instanceof PollerItem) {

			// Execute the item's custom logic and cancel the default villager GUI
			stack.useOnEntity(player, villager, hand);
			cir.setReturnValue(ActionResult.SUCCESS); // 1.21 Fix: Use the Enum constant directly
			return;
		}

		// 2. Radical/Unhappy Villagers Refuse to Trade
		if (!villager.getWorld().isClient()) {
			if (ext.getViewpoint().getHappy() <= 20) {
				sayNo(); // Assuming you have a @Shadow for this
				player.sendMessage(Text.literal(ext.getLegalName() + " is too unhappy to trade!"));

				// 1.21 Fix: Removed .success(boolean), just use the SUCCESS constant
				cir.setReturnValue(ActionResult.SUCCESS);
				return;
			}
		}

		// 3. Update Capitalist Trade Levels BEFORE the GUI Opens
		if (villager.getVillagerData().getProfession() == ModProfessions.CAPITALIST) {
			if (!villager.getWorld().isClient()) {
				Box workerSearch = villager.getBoundingBox().expand(40.0);
				List<VillagerEntity> nearbyWorkers = villager.getWorld().getEntitiesByClass(
						VillagerEntity.class,
						workerSearch,
						v -> v.getVillagerData().getProfession() == ModProfessions.WORKER
				);
				int workerCount = nearbyWorkers.size();

				Box capitalistSearch = villager.getBoundingBox().expand(20.0);
				List<VillagerEntity> nearbyCapitalists = villager.getWorld().getEntitiesByClass(
						VillagerEntity.class,
						capitalistSearch,
						v -> v != villager && v.getVillagerData().getProfession() == ModProfessions.CAPITALIST
				);
				int capitalistCount = nearbyCapitalists.size();

				if (capitalistCount >= 1) {
					// Refuse to trade (reset to level 1) if there are other nearby capitalists
					villager.setVillagerData(villager.getVillagerData().withLevel(1));
				} else {
					// Change trade quality of capitalist based on nearby workers
					if (workerCount < 2) {
						villager.setVillagerData(villager.getVillagerData().withLevel(1));
					} else if (workerCount < 6) {
						villager.setVillagerData(villager.getVillagerData().withLevel(2));
					} else if (workerCount < 11) {
						villager.setVillagerData(villager.getVillagerData().withLevel(3));
					} else if (workerCount < 16) {
						villager.setVillagerData(villager.getVillagerData().withLevel(4));
					} else if (workerCount < 21) {
						villager.setVillagerData(villager.getVillagerData().withLevel(5));
					}
				}
			}
		}
	}

	//become a pillager
	public void criminalize(){
		VillagerEntity villager = (VillagerEntity) (Object) this;
		VillagerExt ext = (VillagerExt) villager;

		//summon criminal at villager location
		VindicatorEntity vindicator = new VindicatorEntity(EntityType.VINDICATOR, villager.getWorld());
		vindicator.refreshPositionAndAngles(
				villager.getX(),
				villager.getY(),
				villager.getZ(),
				villager.getYaw(),
				0f
		);

		//give it villager's old name
		String name = ext.getLegalName();
		vindicator.setCustomName(Text.literal(name));
		vindicator.setCustomNameVisible(true);

		//arm him
		vindicator.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
		vindicator.setCurrentHand(Hand.MAIN_HAND);

		//replace villager
		villager.getWorld().spawnEntity(vindicator);
		villager.discard();
	}

	//nbt storage vars
	private final String legalNameTag = "legalName";
	private String legalName;
	private final String viewpointTag = "viewpoint";
	private Viewpoint viewpoint;

	//nbt initial var generation
	//default startup storage method

	public void genBaseNBT(VillagerProfession prof){
		if (getLegalName()==null){
			String[] names = {"Destiny","Alex","Thaddeus","Flemp","Jocko","Clarence","Splenda","Stephen","Alexei","Connor","Kai","Sean","George","Terrance","Charlie","Brick","Paddy","Morgan","Rick","Joseph","Mark","Mary","Harold","Sue","Jane","Helen","Janice","Josh","Donny","Barry","Melissa","Rose","Drake","Hillary","Lori","Porky","Jim","Mike","Richard","Wilson","Chris","Christina","Sarah","Mitch","Levi","Victoria","Blemish"};
			char[] lInitials = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
			setLegalName(names[new Random().nextInt(names.length)]+", "+ lInitials[new Random().nextInt(lInitials.length)] +".");
		}
		if (getViewpoint() == null) {
			Viewpoint v = new Viewpoint();
			v.generateViewpoint(prof);
			setViewpoint(v);
		}
	}


	//nbt storage methods
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		if (legalName!=null)
			nbt.putString(legalNameTag, legalName);
		if (viewpoint!=null)
			nbt.put(viewpointTag, viewpoint.toNbt());

	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains(legalNameTag)) {
			setLegalName(nbt.getString(legalNameTag));
		}
		if (nbt.contains(viewpointTag)) {
			setViewpoint(Viewpoint.fromNbt(nbt.getCompound(viewpointTag)));
		}
	}

	public String getLegalName() {
		return legalName;
	}
	public void setLegalName(String value) {
		legalName = value;
	}

	public Viewpoint getViewpoint() {
		return viewpoint;
	}
	public void setViewpoint(Viewpoint v) {
		viewpoint = v;
	}


	//socialization and life satisfaction stuff
	private boolean hasMetToday = false;

	public int getMoodOutlook(){
		VillagerEntity villager = (VillagerEntity) (Object) this;
		VillagerExt ext = (VillagerExt) villager;

		//villager wants to not be these
		boolean isHomeless = !ext.hasBed();
		boolean isUnemployed = villager.getVillagerData().getProfession().equals(VillagerProfession.NONE);
		boolean isOvercrowded = (ext.getNearbyVillagerCount(8))>6;

		//bonuses that villages like
		boolean isProtected = ext.isIronGolemNearby(32);
		boolean isSocial = ext.hasMetToday();
		boolean isRetarded = villager.getVillagerData().getProfession().toString().equals("nitwit");
		boolean isSelfEmployed = !villager.getVillagerData().getProfession().equals(VillagerProfession.NONE)
				&& !villager.getVillagerData().getProfession().equals(VillagerProfession.NITWIT)
				&& !villager.getVillagerData().getProfession().equals(ModProfessions.WORKER);

		boolean isGoodAtJob = isSelfEmployed && villager.getVillagerData().getLevel()>=4;



		int moodDelta = 0;

		//bad things decrease mood outlook
		if (isHomeless){moodDelta-=6;}
		if (isUnemployed){moodDelta-=5;}
		if (isOvercrowded){moodDelta-=3;}

		//good things increase mood outlook
		if (isProtected){moodDelta+=1;}
		if (isSocial){moodDelta+=1;}
		if (isRetarded){moodDelta+=2;}
		if (isSelfEmployed){moodDelta+=1;}
		if (isGoodAtJob){moodDelta+=1;}


		return moodDelta;
	}

	public void updateDaily() {
		updateMood();
		updateIdeology();

		resetMetToday();
	}

	public void updateMood(){
		VillagerEntity villager = (VillagerEntity) (Object) this;
		VillagerExt ext = (VillagerExt) villager;

		ext.getViewpoint().deltaHappy(getMoodOutlook());
	}

	public void updateIdeology() {
		VillagerEntity villager = (VillagerEntity) (Object) this;
		if (villager.getWorld().isClient()) return;

		//daily change to change their mind
		if (villager.getRandom().nextInt(100) >= 10) return;

		VillagerExt ext = (VillagerExt) villager;

		int happy = ext.getViewpoint().getHappy();
		Ideology current = ext.getViewpoint().getIdeology();
		Ideology updated = current;

		// --- 1. DETERMINE THEIR CLASS ---
		String economicClass;
		if (villager.getVillagerData().getProfession() == ModProfessions.CAPITALIST) {
			economicClass = "CAPITALIST";
		} else if (villager.getVillagerData().getProfession() == ModProfessions.WORKER) {
			economicClass = "WORKER";
		} else if (villager.getVillagerData().getProfession() != VillagerProfession.NONE) {
			economicClass = "PETITE_BOURG";
		} else {
			economicClass = "UNEMPLOYED";
		}

		// --- 2. APPLY CLASS INTEREST DRIVES ---
		if (happy <= 30) {
			// Misery Radicalizes
			switch (economicClass) {
				case "WORKER" -> updated = current.moveTowards(Ideology.COMMUNIST);
				case "PETITE_BOURG" -> updated = current.moveTowards(Ideology.PROGRESSIVE);
				case "CAPITALIST" -> updated = current.moveTowards(Ideology.LIBERTARIAN);
				case "UNEMPLOYED" -> updated = current.moveTowards(Ideology.FASCIST);
			}
		} else if (happy >= 70) {
			// Success reinforces leading party ideology
			String nation = ext.getViewpoint().getNation();

			if (nation == null || nation.equalsIgnoreCase("(none)")){
				// No nation, so just drift moderate
				updated = current.moveTowards(Ideology.MODERATE);
			} else {
				NationData data = NationData.get((ServerWorld) villager.getWorld());
				String rulingPartyName = data.getRulingParty(nation);

				if (rulingPartyName != null) {
					Party rulingParty = data.getParties(nation).stream()
							.filter(p -> p.getName().equalsIgnoreCase(rulingPartyName))
							.findFirst().orElse(null);

					if (rulingParty != null) {
						updated = current.moveTowards(rulingParty.getIdeology());
					}
				}
			}
		}

		// --- 4. SAVE AND LOG ---
		// If happiness is 31-69, updated == current, so nothing happens (status quo apathy)
		if (updated != current) {
			ext.getViewpoint().setIdeology(updated);

			// Fixed: The database now updates regardless of whether they are happy or miserable
			String nation = ext.getViewpoint().getNation();
			if (nation != null && !nation.equalsIgnoreCase("(none)")) {
				NationData data = NationData.get((ServerWorld) villager.getWorld());
				data.updateVillager(villager.getUuid(), nation, happy, updated);
			}

			Vilid.LOGGER.info(getLegalName() + " (" + economicClass + ") ideology changed from "
					+ current.alignment + " to " + updated.alignment);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		VillagerEntity self = (VillagerEntity) (Object) this;
		if (self.getWorld().isClient()) return;

		// Check if the villager's brain is in MEET activity (socializing)
		if (!hasMetToday() && self.getBrain().hasActivity(Activity.MEET)) {
			VillagerEntity partner = findMeetPartner(self);
			if (partner != null) {
				Vilid.LOGGER.info(getLegalName() + " has socialized today!");
				this.markMetToday();
				socializePolitics(self, partner);
			}
		}
	}

	private VillagerEntity findMeetPartner(VillagerEntity self) {
		double radius = 6.0; // gossip/meet range, tight -- not the same constant as NationUtil's join radius
		Box box = new Box(self.getX() - radius, self.getY() - radius, self.getZ() - radius,
				self.getX() + radius, self.getY() + radius, self.getZ() + radius);
		return self.getWorld().getEntitiesByClass(VillagerEntity.class, box,
						v -> v != self && v.getBrain().hasActivity(Activity.MEET))
				.stream()
				.min((a, b) -> Double.compare(a.squaredDistanceTo(self), b.squaredDistanceTo(self)))
				.orElse(null);
	}

	private void socializePolitics(VillagerEntity self, VillagerEntity partner) {
		VillagerExt selfExt = (VillagerExt) self;
		VillagerExt partnerExt = (VillagerExt) partner;

		ServerWorld world = (ServerWorld) self.getWorld();
		NationData data = NationData.get(world);

		boolean selfNone = !NationUtil.hasNation(self);
		boolean partnerNone = !NationUtil.hasNation(partner);

		if (selfNone && partnerNone) {
			String nation = NationUtil.generateVillageName(self.getBlockPos());

			//temporarily disabled automatic nation founding
//			NationUtil.foundNation(self.getWorld().getServer().getCommandSource(),nation,Ideology.CONSERVATIVE.toString(), data);
//
//			joinNation(selfExt, self, nation, data);
//			joinNation(partnerExt, partner, nation, data);
//			Vilid.LOGGER.info(selfExt.getLegalName() + " and " + partnerExt.getLegalName()
//					+ " founded " + nation + " together");

		} else if (selfNone) {
			String nation = partnerExt.getViewpoint().getNation();
			joinNation(selfExt, self, nation, data);
			Vilid.LOGGER.info(selfExt.getLegalName() + " joined " + nation
					+ " after meeting " + partnerExt.getLegalName());

		} else if (partnerNone) {
			String nation = selfExt.getViewpoint().getNation();
			joinNation(partnerExt, partner, nation, data);
			Vilid.LOGGER.info(partnerExt.getLegalName() + " joined " + nation
					+ " after meeting " + selfExt.getLegalName());
		}
		// both already have nations -> no-op for now, future popularity-comparison hook goes here
	}

	private void joinNation(VillagerExt ext, VillagerEntity v, String nation, NationData data) {
		ext.getViewpoint().setNation(nation);
		data.updateVillager(v.getUuid(), nation, ext.getViewpoint().getHappy(), ext.getViewpoint().getIdeology());
	}

	@Override
	public boolean hasBed() {
		VillagerEntity self = (VillagerEntity) (Object) this;

		if (self.getWorld().isClient()) return false;

		var homeMemory = self.getBrain().getOptionalMemory(MemoryModuleType.HOME);
		if (homeMemory.isEmpty()) {
			// no memory of a bed at all
			return false;
		}

		BlockPos bedPos = homeMemory.get().pos();

		// check that the block at bedPos is still a bed
		if (!self.getWorld().getBlockState(bedPos).isIn(BlockTags.BEDS)) {
			// if the block is no longer a bed → clear memory if you want
			 self.getBrain().forget(MemoryModuleType.HOME);
			return false;
		}

		// memory exists and bed still exists
		return true;
	}


	public boolean isIronGolemNearby( double radius) {
		Entity center = (Entity) (Object) this;
		Box box = new Box(
				center.getX() - radius, center.getY() - radius, center.getZ() - radius,
				center.getX() + radius, center.getY() + radius, center.getZ() + radius
		);

		List<IronGolemEntity> golems = center.getWorld()
				.getEntitiesByClass(IronGolemEntity.class, box, g -> true);

		return !golems.isEmpty();
	}

	//ignores y-level for the most part, so that multi-floor buildings arent penalized
	public int getNearbyVillagerCount(double radius) {
		VillagerEntity villager = (VillagerEntity) (Object) this;
		Box box = new Box(
				villager.getX() - radius, villager.getY() - 1, villager.getZ() - radius,
				villager.getX() + radius, villager.getY() + 1, villager.getZ() + radius
		);

		return villager.getWorld()
				.getEntitiesByClass(VillagerEntity.class, box, v -> !v.equals(villager)) // exclude self
				.size();
	}


	public void markMetToday() {
		this.hasMetToday = true;

		// spawn particles on server side
		VillagerEntity self = (VillagerEntity) (Object) this;

		ServerWorld world = (ServerWorld) self.getWorld();
		world.spawnParticles(
				net.minecraft.particle.ParticleTypes.HEART, // or HAPPY_VILLAGER etc.
				self.getX(), self.getY() + 1, self.getZ(),
				5, // count
				0.5, 0.5, 0.5, // spread
				0.1 // speed
		);

	}

	public boolean hasMetToday() {
		return this.hasMetToday;
	}

	public void resetMetToday() {
		this.hasMetToday = false;
	}
}