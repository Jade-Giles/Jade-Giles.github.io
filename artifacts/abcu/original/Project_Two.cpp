// Project Two - GILES

#include <iostream>
#include <fstream> // for reading the file
#include <sstream> // for splitting the lines
#include <vector> // for storing parts temporarily
#include <string>

using namespace std;

//Define Course structure
struct Course {
	string courseNumber;
	string courseTitle;
	vector<string> prerequisites;
};

//Define Node structure of BST
struct Node {
	Course course;
	Node* left;
	Node* right;

	Node(Course c) {
		course = c;
		left = nullptr;
		right = nullptr;
	}
};

//Define BST class
class CourseBST {
private:
	Node* root;

	void inOrderTraversal(Node* node) {
		if (node == nullptr) {
			return;
		}
		inOrderTraversal(node->left);
		cout << node->course.courseNumber << ": " << node->course.courseTitle << endl;
		inOrderTraversal(node->right);
	}

	Node* insert(Node* node, Course course) {
		if (node == nullptr) {
			return new Node(course);
		}
		if (course.courseNumber < node->course.courseNumber) {
			node->left = insert(node->left, course);
		}
		else {
			node->right = insert(node->right, course);
		}
		return node;
	}

	Course* search(Node* node, string courseNumber) {
		if (node == nullptr) {
			return nullptr;
		}
		if (node->course.courseNumber == courseNumber) {
			return &node->course;
		}
		if (courseNumber < node->course.courseNumber) {
			return search(node->left, courseNumber);
		}
		else {
			return search(node->right, courseNumber);
		}
	}

public:
	CourseBST() {
		root = nullptr;
	}

	void insertCourse(Course course) {
		root = insert(root, course);
	}

	void printCourseList() {
		inOrderTraversal(root);
	}

	void findAndPrintCourse(string courseNumber) {
		Course* course = search(root, courseNumber);
		if (course != nullptr) {
			cout << "Course Number: " << course->courseNumber << endl;
			cout << "Course Title: " << course->courseTitle << endl;
			if (!course->prerequisites.empty()) {
				cout << "Prerequisites: " << endl;
				for (string prereq : course->prerequisites) {
					cout << " -" << prereq << endl;
				}
			}
			else {
				cout << "No Prerequisites" << endl;
			}
		}
		else {
			cout << "Course not found" << endl;
		}
	}

	bool isEmpty() {
		return root == nullptr;
	}
};

//Function to load courses from the file
void loadCoursesFromFile(string fileName, CourseBST& bst) {
	ifstream file(fileName);
	if (!file.is_open()) {
		cout << "Error: Unable to open file" << endl;
		return;
	}

	string line;
	while (getline(file, line)) {
		istringstream ss(line);
		vector<string> parts;
		string part;

		while (getline(ss, part, ',')) {
			parts.push_back(part);
		}

		if (parts.size() >= 2) {
			Course course;
			course.courseNumber = parts[0];
			course.courseTitle = parts[1];

			for (size_t i = 2; i < parts.size(); ++i) {
				course.prerequisites.push_back(parts[i]);
			}

			bst.insertCourse(course); // Add to BST
		}
	}
	file.close();
}

//Display meny options
void displayMenu() {
	cout << "\nMenu Options:" << endl;
	cout << "1. Load course from file" << endl;
	cout << "2. Print list of all courses in alphanumeric order" << endl;
	cout << "3. Print course title and prerequisites" << endl;
	cout << "9. Exit" << endl;
}

int main() {
	CourseBST bst;
	string fileName = "CS 300 ABCU_Advising_Program_Input.txt";
	int choice = 0;

	while (choice != 9) {
		displayMenu();
		cout << "Enter choice: ";
		cin >> choice;

		switch (choice) {
			case 1:
				loadCoursesFromFile(fileName, bst);
				cout << "Courses loaded successfully" << endl;
				break;
			case 2:
				bst.printCourseList();
				break;
			case 3: {
				cout << "Enter course number: ";
				string courseNumber;
				cin >> courseNumber;
				bst.findAndPrintCourse(courseNumber);
				break;
			}
			case 9:
				cout << "Goodbye" << endl;
				break;
			default:
				cout << "Invalid choice" << endl;
		}
	}

	return 0;
}