package com.example.data.local

import com.example.data.model.AcademicSemester
import com.example.data.model.ClassSection
import com.example.data.model.ImportantTopic
import com.example.data.model.NoteItem
import com.example.data.model.PreviousPaper
import com.example.data.model.StudyResource
import com.example.data.model.Subject
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.WebsiteSettings
import com.example.data.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun populateInitialData(dao: NotesDao) = withContext(Dispatchers.IO) {
        // Check if already seeded
        val existingAdmin = dao.getUserByEmail("admin@classnotes.edu")
        if (existingAdmin != null) return@withContext

        // 1. Users (Admin + Student)
        val adminUser = User(
            name = "Prof. Marcus Thorne",
            email = "admin@classnotes.edu",
            passwordHash = PasswordHasher.hashPassword("Admin@123"),
            role = UserRole.ADMIN,
            department = "Computer Science & Engineering",
            semester = "Faculty"
        )
        val studentUser = User(
            name = "Alex Rivera",
            email = "student@university.edu",
            passwordHash = PasswordHasher.hashPassword("Student@123"),
            role = UserRole.STUDENT,
            department = "Computer Science & Engineering",
            semester = "Semester 3"
        )
        dao.insertUser(adminUser)
        dao.insertUser(studentUser)

        // 2. Website Settings
        dao.saveSettings(
            WebsiteSettings(
                websiteName = "Class Notes Hub",
                tagline = "All Your Class Notes in One Place",
                welcomeMessage = "Welcome to Class Notes Hub! Securely access verified lecture notes, previous examination question papers, unit-wise summaries, and curated study materials prepared by university faculty.",
                announcement = "Mid-Semester Examinations approaching! Prioritize highlighted Exam Priority notes and Solved Previous Year Papers.",
                footerText = "© 2026 Class Notes Hub • Official University Study Materials & Class Repository",
                themeColorHex = "#0F2744"
            )
        )

        // 3. Semesters
        val semesters = listOf(
            AcademicSemester(name = "Semester 1", yearName = "Year 1", academicYear = "2025-2026"),
            AcademicSemester(name = "Semester 2", yearName = "Year 1", academicYear = "2025-2026"),
            AcademicSemester(name = "Semester 3", yearName = "Year 2", academicYear = "2025-2026"),
            AcademicSemester(name = "Semester 4", yearName = "Year 2", academicYear = "2025-2026"),
            AcademicSemester(name = "Semester 5", yearName = "Year 3", academicYear = "2025-2026"),
            AcademicSemester(name = "Semester 6", yearName = "Year 3", academicYear = "2025-2026")
        )
        semesters.forEach { dao.insertSemester(it) }

        // 4. Class Sections
        val sections = listOf(
            ClassSection(name = "CSE-A", semesterName = "Semester 1"),
            ClassSection(name = "CSE-B", semesterName = "Semester 1"),
            ClassSection(name = "CSE-A", semesterName = "Semester 2"),
            ClassSection(name = "CSE-A", semesterName = "Semester 3"),
            ClassSection(name = "CSE-B", semesterName = "Semester 3"),
            ClassSection(name = "IT-A", semesterName = "Semester 3"),
            ClassSection(name = "CSE-A", semesterName = "Semester 4"),
            ClassSection(name = "CSE-A", semesterName = "Semester 5")
        )
        sections.forEach { dao.insertClassSection(it) }

        // 5. Subjects
        val subjects = listOf(
            Subject(code = "CS201", name = "Data Structures & Algorithms", semester = "Semester 3", description = "Asymptotic analysis, linear lists, stacks, queues, trees, graphs, sorting & hashing.", credits = 4),
            Subject(code = "CS202", name = "Database Management Systems", semester = "Semester 3", description = "Relational models, ER diagrams, SQL, normalization, ACID transactions, and indexing.", credits = 4),
            Subject(code = "CS203", name = "Operating Systems", semester = "Semester 3", description = "Process scheduling, thread sync, deadlocks, memory management, virtual paging, file systems.", credits = 4),
            Subject(code = "CS204", name = "Computer Networks", semester = "Semester 3", description = "OSI / TCP-IP 5-layer model, packet switching, flow control, routing algorithms, socket API.", credits = 4),
            Subject(code = "CS101", name = "Python Programming", semester = "Semester 1", description = "Procedural & object-oriented programming, data structures, file I/O, exceptions.", credits = 3),
            Subject(code = "CS102", name = "C Programming", semester = "Semester 1", description = "Pointers, memory management, structs, recursion, standard C libraries.", credits = 3),
            Subject(code = "MA201", name = "Discrete Mathematics", semester = "Semester 2", description = "Set theory, propositional logic, graph theory, combinatorics, recurrence relations.", credits = 4),
            Subject(code = "CS301", name = "Software Engineering", semester = "Semester 4", description = "Agile methodologies, SDLC, requirements modeling, design patterns, testing.", credits = 3)
        )
        subjects.forEach { dao.insertSubject(it) }

        // 6. Notes
        val notes = listOf(
            NoteItem(
                title = "Asymptotic Notation & Complexity Analysis",
                description = "Comprehensive notes on Big-O, Omega, and Theta notations with master theorem proofs and 20+ recurrence solved examples.",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 1,
                unitName = "Introduction & Algorithmic Complexity",
                topic = "Asymptotic Analysis",
                tags = "Big-O, Master Theorem, Algorithms, Time Complexity",
                noteType = "Revision Notes",
                priority = "Exam Priority",
                fileName = "DSA_Unit1_Asymptotic_Notation.pdf",
                fileType = "PDF",
                fileSizeFormatted = "3.2 MB",
                filePath = "sample/dsa_unit1.pdf",
                contentPreview = """
# Unit 1: Asymptotic Complexity & Analysis
- Big-O (O): Upper bound on growth rate.
- Omega (Ω): Lower bound on execution steps.
- Theta (Θ): Tight asymptotic bound.
- Master Theorem: T(n) = aT(n/b) + f(n).
- Solved examples of MergeSort, QuickSort best/worst case.
                """.trimIndent(),
                downloadsCount = 142
            ),
            NoteItem(
                title = "Binary Search Trees & Self-Balancing AVL Trees",
                description = "Step-by-step AVL tree rotation diagrams (LL, RR, LR, RL) with insertion, deletion pseudocode and balance factor calculation.",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 3,
                unitName = "Non-Linear Data Structures",
                topic = "AVL & BST Rotations",
                tags = "Trees, AVL, BST, Rotations, Search",
                noteType = "Lecture Notes",
                priority = "Very Important",
                fileName = "DSA_Unit3_AVL_Trees_Guide.pdf",
                fileType = "PDF",
                fileSizeFormatted = "4.8 MB",
                filePath = "sample/dsa_unit3.pdf",
                contentPreview = """
# Unit 3: Tree Data Structures
- Binary Search Tree properties: Left < Node < Right.
- AVL Tree balance factor = height(Left) - height(Right) in {-1, 0, +1}.
- 4 Rotation cases: Single Right (LL), Single Left (RR), Left-Right (LR), Right-Left (RL).
- Worst-case lookup time: O(log n).
                """.trimIndent(),
                downloadsCount = 189
            ),
            NoteItem(
                title = "Graph Traversal & Shortest Path: Dijkstra & Bellman-Ford",
                description = "Unit 4 study material covering adjacency matrix vs list, BFS/DFS applications, Cycle detection, Dijkstra greedy algorithm and negative cycle handling.",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 4,
                unitName = "Graph Algorithms",
                topic = "Shortest Path & MST",
                tags = "Graphs, Dijkstra, Bellman-Ford, Prim, Kruskal",
                noteType = "Class Notes",
                priority = "Exam Priority",
                fileName = "DSA_Unit4_Graph_Algorithms.pdf",
                fileType = "PDF",
                fileSizeFormatted = "5.1 MB",
                filePath = "sample/dsa_unit4.pdf",
                contentPreview = """
# Unit 4: Graph Theory & Shortest Path
- Dijkstra: Min-priority queue implementation O((V + E) log V).
- Cannot handle negative edge weights.
- Bellman-Ford: Dynamic programming O(V * E), detects negative cycles.
- Minimum Spanning Trees: Kruskal (Disjoint Set) vs Prim (Min Heap).
                """.trimIndent(),
                downloadsCount = 210
            ),
            NoteItem(
                title = "Database Normalization (1NF, 2NF, 3NF & BCNF)",
                description = "Complete guide on functional dependencies, candidate keys, lossless decomposition, and normal forms with step-by-step conversion problems.",
                subject = "Database Management Systems",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 2,
                unitName = "Relational Model & Normalization",
                topic = "Functional Dependency & Normal Forms",
                tags = "DBMS, Normalization, 3NF, BCNF, Functional Dependencies",
                noteType = "Revision Notes",
                priority = "Exam Priority",
                fileName = "DBMS_Unit2_Normalization_Mastery.pdf",
                fileType = "PDF",
                fileSizeFormatted = "2.9 MB",
                filePath = "sample/dbms_unit2.pdf",
                contentPreview = """
# Unit 2: Relational Normalization
- 1NF: Atomic values only. No repeating groups.
- 2NF: In 1NF and no partial dependencies (Non-prime attribute dependent on part of candidate key).
- 3NF: In 2NF and no transitive dependencies (X -> Y where Y is non-prime).
- BCNF: For every FD X -> Y, X must be a super key.
                """.trimIndent(),
                downloadsCount = 275
            ),
            NoteItem(
                title = "ACID Properties & Concurrency Control (2PL, Timestamp)",
                description = "Detailed lecture slides on serializability, strict Two-Phase Locking (2PL), deadlock prevention strategies, and write-ahead logging.",
                subject = "Database Management Systems",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 3,
                unitName = "Transaction Management",
                topic = "ACID & 2PL Concurrency",
                tags = "ACID, Transactions, 2PL, Concurrency, Deadlock",
                noteType = "Lecture Notes",
                priority = "Very Important",
                fileName = "DBMS_Unit3_Transactions_ACID.pptx",
                fileType = "PPTX",
                fileSizeFormatted = "6.4 MB",
                filePath = "sample/dbms_unit3.pptx",
                contentPreview = """
# Unit 3: Transaction & ACID
- Atomicity: All or nothing execution (WAL undo).
- Consistency: Database invariant preservation.
- Isolation: Concurrent transactions do not interfere.
- Durability: Committed changes survive system crash.
- Two-Phase Locking: Growing phase (acquire) and Shrinking phase (release).
                """.trimIndent(),
                downloadsCount = 135
            ),
            NoteItem(
                title = "CPU Scheduling Algorithms & Thread Synchronization",
                description = "Comparison of FCFS, SJF, Priority, and Round Robin scheduling with Gantt charts, turnaround time calculations, plus Mutex and Semaphores in C.",
                subject = "Operating Systems",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-B",
                unitNumber = 1,
                unitName = "Processes, Threads & Scheduling",
                topic = "CPU Scheduling & Synchronization",
                tags = "OS, CPU Scheduling, Round Robin, Semaphores, Mutex",
                noteType = "Class Notes",
                priority = "Exam Priority",
                fileName = "OS_Unit1_CPU_Scheduling_Guide.pdf",
                fileType = "PDF",
                fileSizeFormatted = "3.8 MB",
                filePath = "sample/os_unit1.pdf",
                contentPreview = """
# Unit 1: OS Processes & Scheduling
- Process states: New, Ready, Running, Waiting, Terminated.
- Shortest Remaining Time First (SRTF) minimizes average waiting time.
- Round Robin (RR) responsive for time-sharing systems.
- Critical section problem criteria: Mutual exclusion, Progress, Bounded waiting.
                """.trimIndent(),
                downloadsCount = 198
            ),
            NoteItem(
                title = "Deadlock Characterization & Banker's Algorithm",
                description = "Coffman 4 conditions for deadlock, Resource Allocation Graphs (RAG), and Banker's safety state numerical problems with worked solutions.",
                subject = "Operating Systems",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "CSE-B",
                unitNumber = 2,
                unitName = "Deadlocks & Resource Allocation",
                topic = "Deadlock Avoidance",
                tags = "OS, Deadlock, Banker's Algorithm, Safe State",
                noteType = "Question Bank",
                priority = "Exam Priority",
                fileName = "OS_Unit2_Deadlock_Bankers_Algo.docx",
                fileType = "DOCX",
                fileSizeFormatted = "1.9 MB",
                filePath = "sample/os_unit2.docx",
                contentPreview = """
# Unit 2: Deadlocks & Avoidance
- 4 Necessary Conditions: Mutual Exclusion, Hold & Wait, No Preemption, Circular Wait.
- Banker's Algorithm Safety Formula: Need[i][j] = Max[i][j] - Allocation[i][j].
- If Need <= Work, process can terminate and return allocated resources.
                """.trimIndent(),
                downloadsCount = 230
            ),
            NoteItem(
                title = "TCP/IP 5-Layer Model & Subnetting CIDR Calculations",
                description = "Detailed packet headers breakdown (IPv4, IPv6, TCP, UDP), IP addressing, Classless Inter-Domain Routing (CIDR) subnet masks and routing tables.",
                subject = "Computer Networks",
                semester = "Semester 3",
                academicYear = "2025-2026",
                classSection = "IT-A",
                unitNumber = 2,
                unitName = "Network Layer & IP Addressing",
                topic = "Subnetting & CIDR",
                tags = "Networks, TCP/IP, Subnetting, CIDR, IPv4",
                noteType = "Lecture Notes",
                priority = "Important",
                fileName = "Networks_Unit2_Subnetting_CIDR.pdf",
                fileType = "PDF",
                fileSizeFormatted = "4.1 MB",
                filePath = "sample/networks_unit2.pdf",
                contentPreview = """
# Unit 2: Network Layer & Addressing
- IPv4 32-bit address split into Network ID and Host ID.
- CIDR notation /24 provides 256 addresses (254 usable hosts).
- Subnet mask arithmetic: AND operation isolates network address.
- Three-way handshake: SYN -> SYN-ACK -> ACK.
                """.trimIndent(),
                downloadsCount = 164
            ),
            NoteItem(
                title = "Python Object Oriented Programming & Generators",
                description = "Classes, inheritance, dunder methods, custom iterators, generator functions, and decorator patterns with code snippets.",
                subject = "Python Programming",
                semester = "Semester 1",
                academicYear = "2025-2026",
                classSection = "CSE-A",
                unitNumber = 3,
                unitName = "OOP & Advanced Constructs",
                topic = "Classes, Decorators & Generators",
                tags = "Python, OOP, Generators, Decorators, Methods",
                noteType = "Class Notes",
                priority = "Normal",
                fileName = "Python_Unit3_OOP_Generators.pdf",
                fileType = "PDF",
                fileSizeFormatted = "2.4 MB",
                filePath = "sample/python_unit3.pdf",
                contentPreview = """
# Unit 3: Python OOP Architecture
- Classes encapsulate data and behavior via __init__.
- Inheritance: Single, Multiple, Multilevel with super().
- Generators use `yield` keyword to stream lazy memory evaluation.
- Decorators wrap callable objects using @functools.wraps.
                """.trimIndent(),
                downloadsCount = 112
            )
        )
        notes.forEach { dao.insertNote(it) }

        // 7. Previous Year Papers
        val papers = listOf(
            PreviousPaper(
                title = "End Semester Exam Question Paper - Nov 2024",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                classSection = "CSE-A",
                examType = "End Semester",
                examYear = "2024",
                academicYear = "2024-2025",
                description = "Official University End Semester question paper with questions covering AVL trees, Master Theorem, and Graph algorithms.",
                fileName = "CS201_EndSem_2024_Paper.pdf",
                fileType = "PDF",
                fileSizeFormatted = "1.8 MB",
                downloadsCount = 340
            ),
            PreviousPaper(
                title = "Mid Term Examination Paper with Solutions - Sep 2024",
                subject = "Database Management Systems",
                semester = "Semester 3",
                classSection = "CSE-A",
                examType = "Mid Term",
                examYear = "2024",
                academicYear = "2024-2025",
                description = "Complete mid term question paper covering ER modeling, Relational Algebra, and 3NF normalization problems.",
                fileName = "CS202_MidTerm_2024_Solved.pdf",
                fileType = "PDF",
                fileSizeFormatted = "2.3 MB",
                downloadsCount = 280
            ),
            PreviousPaper(
                title = "University Final Exam Paper - Dec 2023",
                subject = "Operating Systems",
                semester = "Semester 3",
                classSection = "CSE-B",
                examType = "End Semester",
                examYear = "2023",
                academicYear = "2023-2024",
                description = "Includes CPU scheduling numericals, Banker's algorithm safe state question, and virtual memory page replacement problem.",
                fileName = "CS203_EndSem_2023_Official.pdf",
                fileType = "PDF",
                fileSizeFormatted = "1.5 MB",
                downloadsCount = 215
            ),
            PreviousPaper(
                title = "Internal Assessment Test 1 - Oct 2024",
                subject = "Computer Networks",
                semester = "Semester 3",
                classSection = "IT-A",
                examType = "Internal",
                examYear = "2024",
                academicYear = "2024-2025",
                description = "Questions on packet switching, OSI layers, framing methods, and CRC polynomial checksum calculation.",
                fileName = "CS204_Internal_2024_Paper.pdf",
                fileType = "PDF",
                fileSizeFormatted = "1.1 MB",
                downloadsCount = 95
            ),
            PreviousPaper(
                title = "Practical Laboratory Examination Guide & Questions - Dec 2024",
                subject = "Python Programming",
                semester = "Semester 1",
                classSection = "CSE-A",
                examType = "Practical",
                examYear = "2024",
                academicYear = "2024-2025",
                description = "Lab practical viva questions and coding problems on OOP, file management, and sorting algorithms.",
                fileName = "CS101_Lab_Practical_2024.pdf",
                fileType = "PDF",
                fileSizeFormatted = "1.4 MB",
                downloadsCount = 145
            )
        )
        papers.forEach { dao.insertPreviousPaper(it) }

        // 8. Important Topics
        val topics = listOf(
            ImportantTopic(
                topicName = "Master Theorem & Recursive Complexity Proofs",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                classSection = "CSE-A",
                unitNumber = 1,
                unitName = "Complexity Analysis",
                description = "High probability exam question. Practice case 1, 2, and 3 along with divide-and-conquer recurrences.",
                priority = "Exam Priority",
                relatedNoteTitle = "Asymptotic Notation & Complexity Analysis"
            ),
            ImportantTopic(
                topicName = "AVL Tree Rotations (LL, RR, LR, RL)",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                classSection = "CSE-A",
                unitNumber = 3,
                unitName = "Non-Linear Structures",
                description = "Numerical problem guaranteed on building AVL tree by sequential element insertions.",
                priority = "Exam Priority",
                relatedNoteTitle = "Binary Search Trees & Self-Balancing AVL Trees"
            ),
            ImportantTopic(
                topicName = "Lossless Join Decomposition & BCNF Normalization",
                subject = "Database Management Systems",
                semester = "Semester 3",
                classSection = "CSE-A",
                unitNumber = 2,
                unitName = "Relational Normalization",
                description = "Determine candidate keys from FD sets and verify whether decomposition preserves dependencies and lossless join.",
                priority = "Exam Priority",
                relatedNoteTitle = "Database Normalization (1NF, 2NF, 3NF & BCNF)"
            ),
            ImportantTopic(
                topicName = "Banker's Algorithm Safety State Calculation",
                subject = "Operating Systems",
                semester = "Semester 3",
                classSection = "CSE-B",
                unitNumber = 2,
                unitName = "Deadlocks",
                description = "Standard 10-mark exam question. Given Allocation, Max, and Available matrices, find the safe execution sequence.",
                priority = "Exam Priority",
                relatedNoteTitle = "Deadlock Characterization & Banker's Algorithm"
            ),
            ImportantTopic(
                topicName = "Dijkstra Algorithm vs Bellman-Ford Shortest Path",
                subject = "Computer Networks",
                semester = "Semester 3",
                classSection = "IT-A",
                unitNumber = 3,
                unitName = "Routing Protocols",
                description = "Link-state vs Distance-vector routing paradigms. Memorize step-by-step distance vector updates.",
                priority = "Very Important",
                relatedNoteTitle = "Graph Traversal & Shortest Path: Dijkstra & Bellman-Ford"
            )
        )
        topics.forEach { dao.insertImportantTopic(it) }

        // 9. Study Resources
        val resources = listOf(
            StudyResource(
                title = "Data Structures & Algorithms Complete Lab Manual",
                resourceType = "Lab Manual",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                description = "Complete lab guide with 15 verified C/C++ laboratory exercises, test cases, and memory diagrams.",
                urlOrPath = "sample/dsa_lab_manual.pdf",
                fileName = "DSA_Lab_Manual_Full_Code.pdf",
                fileSizeFormatted = "5.8 MB",
                isExternalLink = false,
                downloadsCount = 210
            ),
            StudyResource(
                title = "DBMS 300+ Solved University Question Bank",
                resourceType = "Question Bank",
                subject = "Database Management Systems",
                semester = "Semester 3",
                description = "Curated question bank with detailed answers categorized by unit, schema diagrams, and past university questions.",
                urlOrPath = "sample/dbms_question_bank.pdf",
                fileName = "DBMS_Curated_Question_Bank.pdf",
                fileSizeFormatted = "4.2 MB",
                isExternalLink = false,
                downloadsCount = 310
            ),
            StudyResource(
                title = "Stanford CS106B Algorithm Lecture Slide Decks",
                resourceType = "PPT",
                subject = "Data Structures & Algorithms",
                semester = "Semester 3",
                description = "Complete slide presentations explaining recursion, pointers, heaps, and dynamic programming.",
                urlOrPath = "sample/stanford_algorithms_slides.pptx",
                fileName = "Stanford_CS106B_Lecture_Slides.pptx",
                fileSizeFormatted = "12.4 MB",
                isExternalLink = false,
                downloadsCount = 180
            ),
            StudyResource(
                title = "MIT OpenCourseWare Operating Systems Course Portal",
                resourceType = "Useful Website",
                subject = "Operating Systems",
                semester = "Semester 3",
                description = "Official MIT engineering website featuring xv6 operating system source code walkthroughs and video lectures.",
                urlOrPath = "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-828-operating-system-engineering",
                isExternalLink = true,
                downloadsCount = 165
            ),
            StudyResource(
                title = "NPTEL Computer Networks Video Lecture Playlist",
                resourceType = "Video Lecture",
                subject = "Computer Networks",
                semester = "Semester 3",
                description = "40-hour comprehensive video series taught by IIT professors covering physical layer up to application layer.",
                urlOrPath = "https://nptel.ac.in/courses/106/105/106105183",
                isExternalLink = true,
                downloadsCount = 240
            )
        )
        resources.forEach { dao.insertStudyResource(it) }
    }
}
