package employeetracker.model.employee;

import static employeetracker.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import employeetracker.model.employee.exceptions.DuplicateEmployeeException;
import employeetracker.model.employee.exceptions.EmployeeNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of employees that enforces uniqueness between its elements and does not allow nulls.
 * A employee is considered unique by comparing using {@code Employee#isSameEmployee(Employee)}. As such, adding and
 * updating of persons uses Employee#isSameEmployee(Employee) for equality so as to ensure that the employee being added
 * or updated is unique in terms of identity in the UniqueEmployeeList. However, the removal of a employee uses
 * Employee#equals(Object) so as to ensure that the employee with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Employee#isSameEmployee(Employee)
 */
public class UniqueEmployeeList implements Iterable<Employee> {

    private final ObservableList<Employee> internalList = FXCollections.observableArrayList();
    private final ObservableList<Employee> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent employee as the given argument.
     */
    public boolean contains(Employee toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameEmployee);
    }

    /**
     * Adds a employee to the list.
     * The employee must not already exist in the list.
     */
    public void add(Employee toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateEmployeeException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the list.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in the list.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new EmployeeNotFoundException();
        }

        if (!target.isSameEmployee(editedEmployee) && contains(editedEmployee)) {
            throw new DuplicateEmployeeException();
        }

        internalList.set(index, editedEmployee);
    }

    /**
     * Removes the equivalent employee from the list.
     * The employee must exist in the list.
     */
    public void remove(Employee toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new EmployeeNotFoundException();
        }
    }

    public void setEmployees(UniqueEmployeeList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code employees}.
     * {@code employees} must not contain duplicate employees.
     */
    public void setEmployees(List<Employee> employees) {
        requireAllNonNull(employees);
        if (!employeesAreUnique(employees)) {
            throw new DuplicateEmployeeException();
        }

        internalList.setAll(employees);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Employee> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Employee> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueEmployeeList // instanceof handles nulls
                        && internalList.equals(((UniqueEmployeeList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code employees} contains only unique employees.
     */
    private boolean employeesAreUnique(List<Employee> employees) {
        for (int i = 0; i < employees.size() - 1; i++) {
            for (int j = i + 1; j < employees.size(); j++) {
                if (employees.get(i).isSameEmployee(employees.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sort the employees list by field.
     * The employee must exist in the list.
     */
    public void sortEmployee(String field) {
        switch (field) {
        case "n":
            internalList.sort((s1, s2) -> s1.getName().toString().compareToIgnoreCase(s2.getName().toString()));
            break;
        case "s":
            internalList.sort((s1, s2) -> s1.getSalary().value.compareToIgnoreCase(s2.getSalary().value));
            break;
        case "d":
            internalList.sort((s1, s2) -> s1.getDateOfJoining().value.compareToIgnoreCase(s2.getDateOfJoining().value));
            break;
        case "b":
            internalList.sort((s1, s2) -> s1.getDateOfBirth().value.compareToIgnoreCase(s2.getDateOfBirth().value));
            break;
        default:
            throw new AssertionError("Sort field Parameters: TYPE (must be the letter n, s, d or b)\n");
        }
    }

}
