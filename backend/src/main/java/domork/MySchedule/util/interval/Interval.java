package domork.MySchedule.util.interval;

import java.util.*;



// This class implements 'Set' and represents an interval of Integer numbers, specified by the lower and
// upper number of the interval.
public class Interval implements Set {
    private final long lower ;
    private final long upper;
    public Interval(long lower, long upper) {
        this.lower=lower;
        this.upper=upper;
    }

    @Override
    public boolean isContinuous() {
        return true;
    }

    // Returns the union of 'this' and 'other'. If the result can be represented by a single
    // interval (i.e., 'this' and 'other' are connected), the result
    // is of type 'Interval', otherwise it is a 'Union'.
    // Examples:
    // The union of 0-5 and 3-8 is 0-8  (type 'Interval'),
    // the union of 0-5 and 6-7 is 0-7  (type 'Interval'),
    // the union of 0-5 and 7-8 is [0-5, 7-8] (type 'Union').
    @Override
    public Set union(Interval other) {
        if (this.lower>=other.upper+1||this.upper+1<other.lower)
            return new Union(new Interval(this.lower,this.upper),other);
        return new Interval (Math.min(this.lower,other.lower),Math.max(this.upper,other.upper));
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            long curr = lower;
            @Override
            public boolean hasNext() {
                return curr<=upper;
            }

            @Override
            public Long next() {

                return curr++;
            }
        };
    }

    @Override
    public String toString() {
        return lower+"-"+upper;
    }

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }
}