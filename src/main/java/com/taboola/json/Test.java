package com.taboola.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * Question 2:
 * The following class has coding, memory and runtime inefficiencies. Locate and fix as many as you
 * can.
 */
public class Test {
    private Date m_time;
    private String m_name;
    private List<Integer> m_numbers;
    private List<String> m_strings;

    /**
     * For encapsulation, copied the input collections/object and set it as the member variable.
     * This can prevent the misleading. ex: when the input object is modified, the member variable is updated too.
     */
    public Test(Date time, String name, List<Integer> numbers,
                List<String> strings)
    {
        m_time = time == null ? null : new Date(time.getTime());
        m_name = name;
        m_numbers = numbers == null ? null : new ArrayList<>(numbers);
        m_strings = strings == null ? null : new ArrayList<>(strings);
    }

    /**
     * when override the equals method, we should also override the hashCode method.
     * Considering the map which use hashCode to determine the bucket.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(m_name);
    }

    /**
     * compare for the class and then compare the m_name value
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Test test = (Test) obj;

        return Objects.equals(m_name, test.m_name);
    }

    /**
     * handle m_name is null, m_numbers is null
     */
    public String toString() {
        if (m_name == null && m_numbers == null) {
            return "";
        } else if (m_name == null) {
            return m_numbers.toString();
        } else if (m_numbers == null) {
            return m_name;
        } else {
            return m_name + m_numbers.toString();
        }
    }
    public void removeString(String str) {
        m_strings.remove(str);
    }
    public boolean containsNumber(int number) {
        return m_numbers.contains(number);
    }

    /**
     * compare the timeMillis directly. This eliminates the overhead of creating new Date object and the gc.
     */
    public boolean isHistoric() {
        return m_time.getTime() < System.currentTimeMillis();
    }
}