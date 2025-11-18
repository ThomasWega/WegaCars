package me.wega.cars.toolkit.date;

public enum CustomTimeUnit {
    SECONDS {
        @Override
        public long toTicks(long time) {
            return time * 20;
        }
    },
    MINUTES {
        @Override
        public long toTicks(long time) {
            return time * 20 * 60;
        }
    },
    HOURS {
        @Override
        public long toTicks(long time) {
            return time * 20 * 60 * 60;
        }
    },
    TICKS {
        @Override
        public long toTicks(long time) {
            return time;
        }
    };

    public abstract long toTicks(long time);
}
