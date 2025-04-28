package com.taboola.json;

import java.util.Date;
import java.util.List;


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
    public Test(Date time, String name, List<Integer> numbers,
                List<String> strings)
    {
        m_time = time;
        m_name = name;
        m_numbers = numbers;
        m_strings = strings;
    }
    public boolean equals(Object obj) {
        try {
            return m_name.equals(((Test) obj).m_name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String toString() {
        String out = m_name + m_numbers.toString();
        return out;
    }
    public void removeString(String str) {
        m_strings.remove(str);
    }
    public boolean containsNumber(int number) {
        return m_numbers.contains(number);
    }
    public boolean isHistoric() {
        return m_time.before(new Date());
    }
}