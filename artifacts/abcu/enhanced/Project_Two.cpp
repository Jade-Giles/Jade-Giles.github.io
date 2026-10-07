// Project Two - GILES

#include <iostream>
#include <fstream> // for reading the file
#include <sstream> // for splitting the lines
#include <vector> // for storing parts temporarily
#include <string>
#include <algorithm>
#include <unordered_map>

using namespace std;

//Define Course structure
struct Course {
	string courseNumber;
	string courseTitle;
	vector<string> prerequisites;
};

//Define Node structure for AVL tree
struct Node {
	Course course;
	Node* left;
	Node* right;
	int height; 

	Node(Course c) {
		course = c;
		left = nullptr;
		right = nullptr;
		height = 1;
	}
};

//Define AVL tree class
class CourseBST {
private:
	Node* root;
	int getHeight(Node* node) {
		if (node == nullptr) {
			return 0;
		}
		return node->height;
	}

	int getBalance(Node* node) {
		if (node == nullptr) {
			return 0;
		}
		return getHeight(node->left) - getHeight(node->right);
	}

	Node* rotateRight(Node* y) {
		Node* x = y->left;
		Node* temp = x->right;

		// Perform rotation
		x->right = y;
		y->left = temp;

		// Update heights
		y->height = 1 + max(getHeight(y->left), getHeight(y->right));
		x->height = 1 + max(getHeight(x->left), getHeight(x->right));

		return x;
	}

	Node* rotateLeft(Node* x) {
		Node* y = x->right;
		Node* temp = y->left;

		// Perform rotation
		y->left = x;
		x->right = temp;

		// Update heights
		x->height = 1 + max(getHeight(x->left), getHeight(x->right));
		y->height = 1 + max(getHeight(y->left), getHeight(y->right));

		return y;
	}

	void inOrderTraversal(Node* node) {
		if (node == nullptr) {
			return;
		}
		inOrderTraversal(node->left);
		cout << node->course.courseNumber << ": " << node->course.courseTitle << endl;
		inOrderTraversal(node->right);
	}

	Node* insert(Node* node, Course course) {
		// Normal BST insertion
		if (node == nullptr) {
			return new Node(course);
		}
		if (course.courseNumber < node->course.courseNumber) {
			node->left = insert(node->left, course);
		}
		else if (course.courseNumber > node->course.courseNumber) {
			node->right = insert(node->right, course);
		}
		else {
			// Do not insert duplicate course numbers
			return node;
		}

		// Update the height of the node
		node->height = 1 + max(
			getHeight(node->left),
			getHeight(node->right)
		);

		//Check whether this node became unbalanced
		int balance = getBalance(node);

		//Left-left case
		if (balance > 1 &&
			course.courseNumber < node->left->course.courseNumber) {
			return rotateRight(node);
		}

		//Right-right case
		if (balance < -1 &&
			course.courseNumber > node->right->course.courseNumber) {
			return rotateLeft(node);
		}
		// Left-right case
		if (balance > 1 &&
			course.courseNumber > node->left->course.courseNumber) {
			node->left = rotateLeft(node->left);
			return rotateRight(node);
		}
		
		//Right-left case
		if (balance < -1 &&
			course.courseNumber < node->right->course.courseNumber) {
			node->right = rotateRight(node->right);
			return rotateLeft(node);
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

	bool isBalanced(Node* node) {
		if (node == nullptr) {
			return true;
		}

		int balance = getBalance(node);

		if (balance > 1 || balance < -1) {
			return false;
		}

		return isBalanced(node->left) && isBalanced(node->right);
	}

	void buildGraph(
		Node* node,
		unordered_map<string, vector<string>>& graph) {

		if (node == nullptr) {
			return;
		}

		// Add this course and its prereqs to the graph
		graph[node->course.courseNumber] = node->course.prerequisites;

		// Visit the rest of the tree
		buildGraph(node->left, graph);
		buildGraph(node->right, graph);
	}

	bool hasCycleDFS(
		const string& courseNumber,
		unordered_map<string, vector<string>>& graph,
		unordered_map<string, int>& state) {

		// If this course is already in the current path, a cycle exists
		if (state[courseNumber] == 1) {
			return true;
		}

		// If this course has already been fully checked, no need to check again. 
		if (state[courseNumber] == 2) {
			return false;
		}

		// Mark this course as currently being visited
		state[courseNumber] = 1;

		// Visit each prerequisite
		for (const string& prereq : graph[courseNumber]) {
			if (hasCycleDFS(prereq, graph, state)) {
				return true;
			}
		}

		// Finished checking this course
		state[courseNumber] = 2;

		return false;
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

	bool isTreeBalanced() {
		return isBalanced(root);
	}

	bool hasPrerequisiteCycle() {
		unordered_map<string, vector<string>> graph;
		unordered_map<string, int> state;

		// Build the prerequisite graph from the AVL tree
		buildGraph(root, graph);

		//Run DFS starting from each course
		for (const auto& entry : graph) {
			const string& courseNumber = entry.first;

			if (state[courseNumber] == 0) {
				if (hasCycleDFS(courseNumber, graph, state)) {
					return true;
				}
			}
		}
		return false;
	}

	int getTreeHeight() {
		return getHeight(root);
	}
};

//Function to load courses from the file
bool loadCoursesFromFile(string fileName, CourseBST& bst) {
	ifstream file(fileName);
	if (!file.is_open()) {
		cout << "Error: Unable to open file" << endl;
		return false;
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

			bst.insertCourse(course); // Add to AVL tree
		}
	}
	file.close();
	return true;
}

//Display menu options
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
				if (loadCoursesFromFile(fileName, bst)) {
					cout << "Courses loaded successfully" << endl;
					cout << "AVL tree height: " << bst.getTreeHeight() << endl;

					if (bst.isTreeBalanced()) {
						cout << "AVL tree is balanced." << endl;
					}

					else {
						cout << "Warning: AVL tree is not balanced." << endl;
					}

					if (bst.hasPrerequisiteCycle()) {
						cout << "Warning: Prerequisite cycle detected." << endl;
					}

					else {
						cout << "No prerequisite cycles detected." << endl;
					}
				}

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