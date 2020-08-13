(ns flyer.file.core-test
  (:require [flyer.file.core :as file]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test :refer [match?]]
            [test-utils.file :as tu-file])
  (:import java.io.FileNotFoundException))

(deftest assert-file-existence
  (let [test-file-path (tu-file/create-new-test-file! "new-file.txt" nil)
        invalid-path (str test-file-path "invalidate")]
    (testing "File shouldn't exist"
      (is (not (file/exists? invalid-path))))

    (testing "File should exist"
      (is (file/exists? test-file-path)))

    (tu-file/delete-test-file! test-file-path)))

(deftest file-read
  (let [file-content "file-content"
        test-file-path (tu-file/create-new-test-file! "new-file.txt" file-content)
        invalid-path (str test-file-path "invalidate")]
  (testing "Should get file conten as a string"
    (is (match? string? (file/read test-file-path))))

  (testing "File content should match"
    (is (match? file-content (file/read test-file-path))))

  (testing "Should throw when file doesn't exists"
    (is (thrown-match? FileNotFoundException nil (file/read invalid-path))))
  (tu-file/delete-test-file! test-file-path)))

(deftest write-file
  (let [file-data "my-file-content"
        test-file-path (tu-file/create-new-test-file! "new-file.txt" nil)]
    (file/write! test-file-path file-data)
    (testing "Should write in a new file when not exists"
      (is (file/exists? test-file-path)))

    (testing "File data should be persisted"
      (is (match? file-data (file/read test-file-path))))

    (testing "Should update file conten"
      (let [new-data "updated-content"]
        (file/write! test-file-path new-data)
        (is (match? (file/read test-file-path)
                    (str file-data new-data)))))

    (tu-file/delete-test-file! test-file-path)))
