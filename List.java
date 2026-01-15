public class List {

    private Node first;
    private int size;

    public List() {
        first = null;
        size = 0;
    }

    // REQUIRED by tester
    public int getSize() {
        return size;
    }

    // REQUIRED by tester: must return CharData
    public CharData getFirst() {
        return first == null ? null : first.cp;
    }

    public int indexOf(char chr) {
        Node current = first;
        int index = 0;
        while (current != null) {
            if (current.cp.chr == chr)
                return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    public void addFirst(char chr) {
        first = new Node(new CharData(chr), first);
        size++;
    }

    public boolean remove(char chr) {
        if (first == null)
            return false;

        if (first.cp.chr == chr) {
            first = first.next;
            size--;
            return true;
        }

        Node current = first;
        while (current.next != null) {
            if (current.next.cp.chr == chr) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public CharData get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        Node current = first;
        for (int i = 0; i < index; i++)
            current = current.next;

        return current.cp;
    }

    public ListIterator listIterator(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();

        Node current = first;
        for (int i = 0; i < index; i++)
            current = current.next;

        return new ListIterator(current);
    }

    public void update(char chr) {
        int idx = indexOf(chr);
        if (idx == -1)
            addFirst(chr);
        else
            get(idx).count++;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        Node current = first;
        while (current != null) {
            sb.append(current.cp);
            if (current.next != null)
                sb.append(" ");
            current = current.next;
        }
        sb.append(")");
        return sb.toString();
    }
}
