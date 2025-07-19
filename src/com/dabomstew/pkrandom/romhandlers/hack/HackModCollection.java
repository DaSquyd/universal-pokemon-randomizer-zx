package com.dabomstew.pkrandom.romhandlers.hack;

import java.util.*;

public class HackModCollection<T extends HackMod> extends HackMod {
    public final List<T> hackMods;
    private final Set<Class<? extends HackMod>> dependencies;

    public HackModCollection(List<T> hackMods) {
        super();

        this.dependencies = new HashSet<>();

        // Sort
        this.hackMods = new ArrayList<>(hackMods.size());
    }

    public final void addHackMods(List<? extends T> newHackMods) {
        Set<Class<? extends HackMod>> newHackModsSet = new HashSet<>(newHackMods.size());
        for (T newHackMod : newHackMods) {
            if (newHackModsSet.contains(newHackMod.getClass()))
                throw new RuntimeException("Duplicate type found"); // TODO: Replace instead?
            
            newHackModsSet.add(newHackMod.getClass());
        }
        
        hackMods.removeIf((i) -> newHackModsSet.contains(i.getClass()));
        hackMods.addAll(newHackMods);
    }

    public void removeHackMod(Class<? extends T> hackModClass) {
        hackMods.removeIf((i) -> i.getClass() == hackModClass);
    }

    public T getHackMod(Class<T> hackModClass) {
        for (T hackMod : hackMods) {
            if (hackMod.getClass() == hackModClass)
                return hackMod;
        }

        return null;
    }

    @Override
    public void Merge(HackMod other) {
        if (other instanceof HackModCollection<?> otherCollection) {
            for (var test : otherCollection.hackMods) {
                hackMods.removeIf((i) -> i.getClass() == test.getClass());
                hackMods.add((T)test);
            }
        }
    }

    @Override
    public Set<Class<? extends HackMod>> getDependencies() {
        return dependencies;
    }

    @Override
    public void apply(Context context) {
        sortHackMods();

        for (T hackMod : hackMods) {
            if (context.applied().containsKey(hackMod.getClass()))
                throw new RuntimeException();

            hackMod.apply(context);
            context.applied().put(hackMod.getClass(), hackMod);
        }
    }

    protected void sortHackMods() {
        sortHackMods(null);
    }

    protected void sortHackMods(Comparator<HackMod> comparator) {
        if (comparator != null)
            hackMods.sort(comparator);

        LinkedList<T> tempHackMods = new LinkedList<>(hackMods);
        this.hackMods.clear();

        Map<HackMod, Set<Class<? extends HackMod>>> tempDependencies = new HashMap<>();
        for (HackMod hackMod : tempHackMods) {
            Set<Class<? extends HackMod>> dependencies = hackMod.getDependencies();
            this.dependencies.addAll(dependencies);
            if (!dependencies.isEmpty())
                tempDependencies.put(hackMod, new HashSet<>(dependencies));
        }

        Set<Class<? extends HackMod>> usedModClasses = new HashSet<>();

        while (!tempHackMods.isEmpty()) {
            // used for error checking
            int addedThisIteration = 0;
            Iterator<T> iterator = tempHackMods.iterator();
            while (iterator.hasNext()) {
                T hackMod = iterator.next();

                if (tempDependencies.containsKey(hackMod)) {
                    Set<Class<? extends HackMod>> dependencies = tempDependencies.get(hackMod);
                    dependencies.removeIf(usedModClasses::contains);

                    if (!dependencies.isEmpty())
                        continue;

                    tempDependencies.remove(hackMod);
                }

                // no remaining dependencies, add
                iterator.remove();
                usedModClasses.add(hackMod.getClass());
                this.hackMods.add(hackMod);
                this.dependencies.remove(hackMod.getClass()); // we don't need to worry about
                ++addedThisIteration;
            }

            if (addedThisIteration == 0)
                throw new RuntimeException("missing dependency!");
        }
    }
}
