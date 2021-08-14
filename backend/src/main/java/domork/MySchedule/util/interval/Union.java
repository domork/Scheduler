package domork.MySchedule.util.interval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// 'Union' implements 'Set' and represents a union of multiple (at least two) unconnected intervals.
public class Union implements Set {
    private final List<Interval> list = new ArrayList<>();

    public Union(Interval a, Interval b) {

            list.add(a);
            list.add(b);

    }

    public Union() {

    }

    // Helper method: Removes all intervals from this 'Union' which are connected to 'other'
    // (i.e., intervals where the union with 'other' is a single continuous interval).
    // The union of all the removed intervals and 'other' is returned.
    // Precondition: other != null.
    public Interval removeAllConnectedWith(Interval other) {
        ArrayList<Interval> tempList = new ArrayList<>();
        for (Interval i : list) {
            if (i.union(other).isContinuous()) {
                tempList.add(i);
            }
        }
        Interval ret = other;
        for (Interval i : tempList) {
            this.list.remove(i);
            ret = (Interval) ret.union(i);
        }
        return ret;
    }

    @Override
    public boolean isContinuous() {
        return list.size() == 1;
    }

    //add to list
    @Override
    public Set union(Interval other) {
        list.add(removeAllConnectedWith(other));
        return this;
    }

    @Override
    public Iterator<Long> iterator() {
        Iterator<Interval> iterator = list.iterator();
        return new Iterator<Long>() {
            private Iterator<Long> iter = iterator.next().iterator();


            @Override
            public boolean hasNext() {
                return iterator.hasNext() || iter.hasNext();
            }

            @Override
            public Long next() {
                if (iter.hasNext())
                    return iter.next();
                iter = iterator.next().iterator();
                return iter.next();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        for (Interval i : list) {
            s.append(i.toString()).append(", ");
        }
        if (s.length() > 1) {
            s.delete(s.length() - 2, s.length());
        }
        s.append("]");
        return s.toString();
    }

    public List<Interval> getList() {
        return list;
    }
}
